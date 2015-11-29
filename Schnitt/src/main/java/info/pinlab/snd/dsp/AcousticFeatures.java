package info.pinlab.snd.dsp;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.trs.PrimitiveDoubleTier;

public class AcousticFeatures {

	
	
	static public PrimitiveDoubleTier getAmplitude(WavClip wav){
		int [] sample = wav.toIntArray();
		double [] amps = new double[sample.length];
		//-- calc amplitude
		for(int i =0; i< sample.length; i++){
			amps[i] = Math.abs(sample[i])/32768.0d;  //-- 16bit signed wav
		}
			
		PrimitiveDoubleTier tier = new PrimitiveDoubleTier(amps);
		return tier;
	}
	
	
	
	
	static public PrimitiveDoubleTier getPitch(WavClip wav){
//		PrimitiveDoubleTier tier = new PrimitiveDoubleTier();
		//-- calc amplitude
		return null;
	}
	
	
}
