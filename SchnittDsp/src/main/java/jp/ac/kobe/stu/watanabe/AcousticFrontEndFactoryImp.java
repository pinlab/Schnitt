package jp.ac.kobe.stu.watanabe;

public class AcousticFrontEndFactoryImp implements AcousticFrontEndFactory {

	private int FFT_N;
	private int MFCC_CH;
	private int stepLength; /* ms */
	private int HZ;
	private int winLen;
	private String windowType;

	public AcousticFrontEndFactoryImp(int fft_n, int mfcc_ch, String win, int winLen, int step, int hz) {
		this.FFT_N = fft_n;
		this.MFCC_CH = mfcc_ch;
		this.windowType = win;
		this.stepLength = step;
		this.winLen = winLen;
		this.HZ = hz;
	}

	/**
	 * set the number of fft
	 * 
	 * @param int
	 *            the number of fft
	 */
	@Override
	public AcousticFrontEndFactory setFftN(int n) {
		this.FFT_N = n;
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
		this.MFCC_CH = n;
		return this;
	}

	/**
	 * set the windowning type
	 * 
	 * @param String
	 *            windowning type
	 */
	@Override
	public AcousticFrontEndFactory setWindowType(String w) {
		this.windowType = w;
		return this;
	}

	/**
	 * set the windowning length for executing stft
	 * 
	 * @param int
	 *            the length of window
	 */
	@Override
	public AcousticFrontEndFactory setWindowLength(int wl) {
		this.winLen = wl;
		return this;
	}

	/**
	 * set the length of step for executing stft
	 * 
	 * @param int
	 *            stepLength
	 */
	@Override
	public AcousticFrontEndFactory setStepLength(int s) {
		this.stepLength = s;
		return this;
	}

	/**
	 * set the sampling Frequency
	 * 
	 * @param int
	 *            HZ
	 */
	@Override
	public AcousticFrontEndFactory setHz(int hz) {
		this.HZ = hz;
		return this;
	}

	/**
	 * Build a new class
	 */
	@Override
	public AcousticFrontEnd build() {
		return new AcousticFrontEndImp(this.FFT_N, this.MFCC_CH, this.windowType, this.winLen, this.stepLength,
				this.HZ);
	}
}
