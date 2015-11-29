package info.pinlab.snd.vad;


/**
 * 
 * 
 * @author kinoko
 *
 */
public interface VoiceActivityDetectorFactory{

	public <T> VoiceActivityDetectorFactory setVadParam(VadParam<T> param, T value);
	public <T> VoiceActivityDetectorFactory setVadParam(String paramName,  T value);
	
	public VoiceActivityDetector build();
}
