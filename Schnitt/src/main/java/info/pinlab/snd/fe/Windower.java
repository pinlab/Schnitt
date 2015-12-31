package info.pinlab.snd.fe;

public interface Windower{
	public enum WindowType{
		HANNING, HAMMING,
		NONE
	}
	/**
	 * In-place filter (no copies)
	 * @param samples
	 */
	public void filter(double [] samples);
	public WindowType getWindowType();
	
}
