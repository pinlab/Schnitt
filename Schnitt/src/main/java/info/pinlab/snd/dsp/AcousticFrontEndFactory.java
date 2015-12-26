package info.pinlab.snd.dsp;

import info.pinlab.snd.dsp.Window.WindowType;

public interface AcousticFrontEndFactory {

	public AcousticFrontEndFactory setSamplingRate(int hz);
	
	public AcousticFrontEndFactory setFrameLenInMs(int winLen);
	
	public AcousticFrontEndFactory setFrameShiftInMs(int step);

	/**
	 * 
	 * @param win type of window to filter frames (see {@link WindowType}, Hanning, Hamming...etc)
	 * @return
	 */
	public AcousticFrontEndFactory setWindowType(WindowType win);

	public AcousticFrontEndFactory setFftN(int fft);
	
	public AcousticFrontEndFactory setMfccCh(int mfcc);

	public AcousticFrontEnd build();

}
