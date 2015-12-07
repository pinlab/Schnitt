package info.pinlab.snd.vad;

import java.util.List;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.trs.BinaryTier;

public interface VoiceActivityDetector {
	/**
	 * 
	 * @return {@link BinaryTier} with voice activity as True
	 */
	public BinaryHypoTier getVoiceActivity(WavClip wav);
	
	public List<VadParam<?>> getParams();
	
}
