package info.pinlab.snd.dsp;


/**
 * 
 * @author Hiroki Watanabe
 *
 */
public class MelFilterBank {
	private final int hz;
	private final int fftN;
	private final int mfccChN;

	private double fmax; // Nyquist
	private double melMax; // Mel-Nyquist
	private int nmax; // Maximum Number of Frequency Index
	private double df; // Frequency Resolution
	private double dmel; // Interval of center of FilterBank in MelScale

	private final double[][] filterBank ;

	/**
	 * 
	 * @param fft_n  point fft
	 * @param fs     sampling rate (frame/sec) in Hz
	 * @param mfcc_ch
	 */
	public MelFilterBank(AcousticContext context) {
		this.hz = context.hz;
		this.mfccChN = context.mfccCh;
		this.fftN = context.fftN;
		
		this.fmax = hz / 2; // Nyquist
		this.melMax = 1127.01048 * Math.log(fmax / 700.0 + 1.0); // Mel-Nyquist
		this.nmax = fftN / 2; // Maximum Number of Frequency Index
		this.df = hz / fftN; // Frequency Resolution
		this.dmel = melMax / (mfccChN + 1); // Interval of center of FilterBank
		// in MelScale
		
		
		// Calculating Center of FilterBank in MelScale.
		double[] melCenter = new double[mfccChN];
		for (int i = 1; i < mfccChN + 1; i++) {
			melCenter[i - 1] = i * dmel;
		}
		
		// Translate Center of FilterBank in MelScale into Hz!
		double[] fcenter = new double[mfccChN];
		for (int i = 0; i < mfccChN; i++) {
			fcenter[i] = 700.0 * (Math.exp(melCenter[i] / 1127.01048) - 1.0);
		}
		
		// Translate Center of FilterBank in MelScale into Frequency INDEX
		// (BIN).
		double[] indexCenter = new double[mfccChN];
		for (int i = 0; i < mfccChN; i++) {
			indexCenter[i] = Math.round(fcenter[i] / df);
		}
		
		double[] indexStart = new double[mfccChN];
		indexStart[0] = 0;
		for (int i = 0; i < mfccChN - 1; i++) {
			indexStart[i + 1] = indexCenter[i];
		}
		
		double[] indexStop = new double[mfccChN];
		for (int i = 0; i < mfccChN - 1; i++) {
			indexStop[i] = indexCenter[i + 1];
		}
		indexStop[indexStop.length - 1] = nmax;
		
		// Creating mel-FilterBank
		filterBank = new double[mfccChN][nmax];
		for (int c = 0; c < mfccChN; c++) {
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
	}

	/**
	 * Calculates Mel-Filter bank.
	 * 
	 * @param samples
	 * @return mfcc_ch number of mfc coefficients
	 */
	public double[] process(double[] samples) {
		// FilteredAmp = Amplitude array that is applied mel-FilterBank from CH1
		// = MFCC_CH to
		double[][] melFilterBankedAmp = new double[mfccChN][fftN / 2];
		for (int c = 0; c < mfccChN; c++) {
			for (int i = 0; i < fftN / 2; i++) {
				melFilterBankedAmp[c][i] = samples[i] * filterBank[c][i];
			}
		}

		/*
		 * Calculating Sum of FilteredAmp
		 */
		double[] sumOfFilteredAmp = new double[mfccChN];
		double sumValue = 0;
		for (int c = 0; c < mfccChN; c++) {
			sumValue = 0;
			// Calc. sum of FilterBanked Amp;
			for (int i = 0; i < fftN / 2; i++) {
				sumValue += melFilterBankedAmp[c][i];
				// System.err.println("sum: " + c + "; " + sumValue);

			}
			sumOfFilteredAmp[c] = sumValue;
		}

		return sumOfFilteredAmp;
	}
}
