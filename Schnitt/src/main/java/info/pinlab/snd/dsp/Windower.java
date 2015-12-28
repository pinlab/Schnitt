package info.pinlab.snd.dsp;

public interface Windower{
	public enum WindowType{
		HANNING, HAMMING
	}

	/**
	 * In-place filter (no copies)
	 * @param samples
	 */
	public void filter(double [] samples);
	public WindowType getWindowType();
	
}
