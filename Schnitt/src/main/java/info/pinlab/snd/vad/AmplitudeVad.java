package info.pinlab.snd.vad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.dsp.AcousticFeatures;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.PrimitiveDoubleTier;
import info.pinlab.snd.trs.Tier.Type;

/**
 * 
 * VAD based on amplitude level.
 * 
 * @author Gabor Pinter
 *
 */
public class AmplitudeVad implements VoiceActivityDetector {
	 static List<VadParam<?>> params;
	
	 static Map<String, VadParam<?>> paramMap = new HashMap<String, VadParam<?>>(); 
	 double thresh = 0.3;//((VadParam<Double>)paramMap.get("AMP_THRESH")).getValue();

	 
	 
	 static{
		 params = new ArrayList<VadParam<?>>();
		 VadParam<Double> param = new VadParam<Double>("AMP_THRESH", Double.class);
		 param.setMinVal(0.0d);
		 param.setParamVal(0.75d);
		 param.setMaxVal(1.0d);
		 params.add(param);
		 paramMap.put(param.getParamName(), param);
	 }
	
	 
	 
	public AmplitudeVad(){
	}

	
	
	
	
	@Override
	public BinaryHypoTier getVoiceActivity(WavClip wav) {
		VadParam<?> param = paramMap.get("AMP_THRESH");
		if(param!=null){
			thresh = (double) ((VadParam<Double>)param).getValue();
		}
		
		BinaryHypoTier activityTier = new BinaryHypoTier();
		activityTier.setName("hypo");
		activityTier.addInterval(0, wav.getDurInMs()/1000.0d, false);
		double hz = wav.getAudioFormat().getSampleRate(); 
		
		
//		double [] sample = wav.toDoubleArray();
		int [] sampleAsInt = wav.toIntArray();
		double [] sample = new double[sampleAsInt.length]; 
		for(int i = 0 ; i < sampleAsInt.length ; i++){
			sample[i] = Math.abs(sampleAsInt[i])/ 16384.0d;
		}
		
		int prevIx = 0;
		boolean isActive = false;
		int i = 0;
		for(; i < sample.length;i++){
//			System.out.println(sample[i] + " " + thresh);
			if(sample[i] >= thresh){
				if(isActive){ 
					//-- already 'active' by hypo -> do nothing
				}else{ //-- it was non-active
					double from = prevIx / hz;
					double to = i / hz;
					activityTier.addInterval(from, to, false);
					isActive = true;
					prevIx = i;
//					System.out.println("ON " + from + "-" + to + "  (" + sample[i] + " > " +thresh);
				}
			}else{//-- sample < thresh
				if(isActive){ 
					isActive = false; 
					double from = prevIx / hz;
					double to = i / hz;
					activityTier.addInterval(from, to, true);
					prevIx = i;
//					System.out.println("OFF " + from + "-" + to);
				}else{ //-- it was non-active
					//-- already 'non-active' by hypo -> do nothing
				}
			}
		}
		double from = prevIx / hz;
		double to = i / hz;
		activityTier.addInterval(from, to, isActive);
		
		return activityTier;
	}

	
	
	@Override
	public List<VadParam<?>> getParams() {
		return params;
	}

}
