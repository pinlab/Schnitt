package info.pinlab.snd.vad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.dsp.AcousticFeatures;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.PrimitiveDoubleTier;

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

	 
	 static{
		 params = new ArrayList<VadParam<?>>();
		 VadParam<Double> param = new VadParam<Double>("AMP_THRESH", Double.class);
		 param.setMinVal(0.0d);
		 param.setMaxVal(1.0d);
		 params.add(param);
		 paramMap.put(param.getParamName(), param);
	 }
	 
	public AmplitudeVad(){
		
	}
	
	
	@Override
	public BinaryTier getVoiceActivity(WavClip wav) {
		BinaryTier activityTier = new BinaryTier();
		activityTier.addInterval(0, wav.getDurInMs()/1000.0d, false);
		
		PrimitiveDoubleTier tier = AcousticFeatures.getAmplitude(wav);
		
		VadParam<Double> par = (VadParam<Double>)paramMap.get("AMP_THRESH");
		double thresh = par.getValue();
		
		PrimitiveDoubleTier amp = AcousticFeatures.getAmplitude(wav);
		double [] points = amp.getPoints();
		for(int i  = 0 ; i < points.length ; i++){
			//-- add logic here
		}
		return activityTier;
	}

	
	
	@Override
	public List<VadParam<?>> getParams() {
		return params;
	}

}
