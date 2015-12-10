package jp.ac.kobe.stu.watanabe;

public class MelFilterBank {

	public double[] ampSamples;
	public double[][] melFilterBankedAmp;
	public double[] sumOfFilteredAmp;
	private final int FS;
	private final int FFT_N;
	private final int MFCC_CH;

	private double fmax; // Nyquist
	private double melMax; // Mel-Nyquist
	private int nmax; // Maximum Number of Frequency Index
	private double df; // Frequency Resolution
	private double dmel; // Interval of center of FilterBank in MelScale

	/**
	 * 
	 * @param fft_n
	 *            point fft
	 * @param fs
	 *            sampling rate (frame/sec) in Hz
	 * @param mfcc_ch
	 */
	public MelFilterBank(int fft_n, int fs, int mfcc_ch) {
		this.FS = fs;
		this.MFCC_CH = mfcc_ch;
		this.FFT_N = fft_n;
	}

	/**
	 * Calculates Mel-Filter bank.
	 * 
	 * @param samples
	 * @return
	 */
	public double[] doMelFilterBank(double[] samples) {
		this.ampSamples = samples;
		this.fmax = FS / 2; // Nyquist
		this.melMax = 1127.01048 * Math.log(fmax / 700.0 + 1.0); // Mel-Nyquist
		this.nmax = FFT_N / 2; // Maximum Number of Frequency Index
		this.df = FS / FFT_N; // Frequency Resolution
		this.dmel = melMax / (MFCC_CH + 1); // Interval of center of FilterBank
											// in MelScale

		// Calculating Center of FilterBank in MelScale.
		double[] melCenter = new double[MFCC_CH];
		for (int i = 1; i < MFCC_CH + 1; i++) {
			melCenter[i - 1] = i * dmel;
		}

		// Translate Center of FilterBank in MelScale into Hz!
		double[] fcenter = new double[MFCC_CH];
		for (int i = 0; i < MFCC_CH; i++) {
			fcenter[i] = 700.0 * (Math.exp(melCenter[i] / 1127.01048) - 1.0);
		}

		// Translate Center of FilterBank in MelScale into Frequency INDEX
		// (BIN).
		double[] indexCenter = new double[MFCC_CH];
		for (int i = 0; i < MFCC_CH; i++) {
			indexCenter[i] = Math.round(fcenter[i] / df);
		}

		double[] indexStart = new double[MFCC_CH];
		indexStart[0] = 0;
		for (int i = 0; i < MFCC_CH - 1; i++) {
			indexStart[i + 1] = indexCenter[i];
		}

		double[] indexStop = new double[MFCC_CH];
		for (int i = 0; i < MFCC_CH - 1; i++) {
			indexStop[i] = indexCenter[i + 1];
		}
		indexStop[indexStop.length - 1] = nmax;

		// Creating mel-FilterBank
		double[][] filterBank = new double[MFCC_CH][nmax];
		for (int c = 0; c < MFCC_CH; c++) {
			// Calculating Slope of Left Side of FilterBank
			double increment = 1.0 / (indexCenter[c] - indexStart[c]);
			double incrementRange = indexCenter[c] - indexStart[c];
			int startIn = (int) indexStart[c];

			for (int i = 0; i < incrementRange; i++) {
				filterBank[c][startIn] = i * increment;
				startIn++;
			}

			double decrement = 1.0 / (indexCenter[c] - indexStop[c]);
			double decrementRange = (indexStop[c]) - indexCenter[c];
			int startDec = (int) indexCenter[c];
			for (int i = 0; i < decrementRange; i++) {
				filterBank[c][startDec] = i * decrement + 1;
				startDec++;
			}
		}

		// FilteredAmp = Amplitude array that is applied mel-FilterBank from CH1
		// = MFCC_CH to
		melFilterBankedAmp = new double[MFCC_CH][FFT_N / 2];
		for (int c = 0; c < MFCC_CH; c++) {
			for (int i = 0; i < FFT_N / 2; i++) {
				melFilterBankedAmp[c][i] = ampSamples[i] * filterBank[c][i];
			}
		}

		/*
		 * Calculating Sum of FilteredAmp
		 */
		sumOfFilteredAmp = new double[MFCC_CH];
		double sumValue = 0;
		for (int c = 0; c < MFCC_CH; c++) {
			sumValue = 0;
			// Calc. sum of FilterBanked Amp;
			for (int i = 0; i < FFT_N / 2; i++) {
				sumValue += melFilterBankedAmp[c][i];
				// System.err.println("sum: " + c + "; " + sumValue);

			}
			sumOfFilteredAmp[c] = sumValue;
		}

		return sumOfFilteredAmp;
	}
}
