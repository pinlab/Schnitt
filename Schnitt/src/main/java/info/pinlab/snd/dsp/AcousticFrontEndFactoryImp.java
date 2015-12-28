package info.pinlab.snd.dsp;

import info.pinlab.snd.dsp.Windower.WindowType;

public class AcousticFrontEndFactoryImp implements AcousticFrontEndFactory {
	//-- default values --//
	public static int HZ = 16000;
	public static int FFT_N = 128;
	public static int MFCC_CH = 26;
	public static int FRAME_LEN_MS = 20;
	public static int FRAME_SHIFT_LEN_MS = 10;
	public static WindowType WIN_TYPE = Windower.WindowType.HANNING;
	
	private int hz = HZ;
	private int fftN = FFT_N;
	private int mfccCh = MFCC_CH;
	private int frameLenInMs = FRAME_LEN_MS;
	private int frameShiftInMs = FRAME_SHIFT_LEN_MS;
	private WindowType winType = WIN_TYPE;

	
	private AcousticFrontEndFactoryImp(){}

	/**
	 * set the number of fft
	 * 
	 * @param int  FFT point size
	 */
	@Override
	public AcousticFrontEndFactory setFftN(int n) {
		fftN = n;
		return this;
	}

	/**
	 * set the number of mfcc channels
	 * 
	 * @param int
	 *            the number of mfcc channels
	 */
	@Override
	public AcousticFrontEndFactory setMfccCh(int n) {
		mfccCh = n;
		return this;
	}

	@Override
	public AcousticFrontEndFactory setWindowType(WindowType win) {
		winType = win;
		return this;
	}

	/**
	 * set the windowning length for executing stft
	 * 
	 * @param int
	 *            the length of window
	 */
	@Override
	public AcousticFrontEndFactory setFrameLenInMs(int ms){
		frameLenInMs = ms;
		return this;
	}

	/**
	 * set the length of step for executing stft
	 * 
	 * @param int
	 *            stepLength
	 */
	@Override
	public AcousticFrontEndFactory setFrameShiftInMs(int ms){
		frameShiftInMs = ms;
		return this;
	}

	/**
	 * set the sampling Frequency
	 * 
	 * @param int
	 *            HZ
	 */
	@Override
	public AcousticFrontEndFactory setSamplingRate(int hz) {
		this.hz = hz;
		return this;
	}

	/**
	 * Build a new class
	 */
	@Override
	public AcousticFrontEnd build() {
		return new AcousticFrontEndImp(fftN, mfccCh, winType,
				this.frameLenInMs, this.frameShiftInMs, hz);	
	}
}
