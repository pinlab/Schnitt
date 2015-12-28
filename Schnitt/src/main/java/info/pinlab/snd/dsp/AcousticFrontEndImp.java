package info.pinlab.snd.dsp;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import info.pinlab.snd.dsp.AcousticContext.AcousticContextBuilder;
import info.pinlab.snd.dsp.Windower.WindowType;

/**
 * 
 * 
 * @author Hiroki Watanabe
 * @version 1.0
 * @since 2015
 */

public class AcousticFrontEndImp implements AcousticFrontEnd {
	private final int MFCC_CEPS_N = 12;
	private final int HZ;
	private final int FFT_N;
	private final int MFCC_CH;

	private final int stepLength;
	private final WindowType windowType;
	private final int frameLenInMs;
	private final int windowSampNum;
	private final int stepSampNum;
	private static int shiftIx;

	private boolean running = true;

	private int totalShiftNum;
	private int[] paddedSamples = null;
	private int[] totalSamples = null;
	private double[] preEmphSamp = null;
	private double[] windowedSamp = null;
	private double[] ampSamples = null;
	private int[] tempSamples;

	public static final DftNormalization STANDARD = DftNormalization.STANDARD;
	public static final TransformType FORWARD = TransformType.FORWARD;

	/**
	 * Setting Acoustic Front End parameters
	 * 
	 * @param fftN
	 * @param mfccCh
	 * @param win
	 * @param winLen
	 * @param step
	 * @param hz
	 */
	public AcousticFrontEndImp(int fftN, int mfccCh, 
			WindowType win, int winLen, int step, int hz) {
		this.FFT_N = fftN;
		this.MFCC_CH = mfccCh;
		this.windowType = win;
		this.frameLenInMs = winLen;
		this.stepLength = step;
		this.HZ = hz;

		this.windowSampNum = (int) (HZ * (frameLenInMs / (1.0 * 1000)));
		this.stepSampNum = (int) (HZ * (stepLength / (1.0 * 1000)));
	}

	/**
	 * Set integer samples of a wavfile Integer sample is 0-padded in order to
	 * match the length of STFT(short-time Fourier transform )
	 * 
	 * @param int
	 *            [] samples
	 * 
	 */
	@Override
	public void setSamples(int[] samples) {
		this.totalSamples = samples;

		/*
		 * windowedSamp : 0 padded windowedSamp's length is set to FFTN
		 */
		this.tempSamples = new int[FFT_N];
		Arrays.fill(this.tempSamples, 0);
		System.arraycopy(totalSamples, 0, tempSamples, 0, windowSampNum);

		/*
		 * 0-padding totalSamples for matching STFT.
		 */
		int restOfSamples = (totalSamples.length - frameLenInMs) % stepLength;
		int paddedSampleLen = totalSamples.length + (stepLength - restOfSamples);
		this.paddedSamples = new int[paddedSampleLen];
		Arrays.fill(this.paddedSamples, 0);
		System.arraycopy(totalSamples, 0, this.paddedSamples, 0, totalSamples.length);

		/*
		 * Get Number of Times of Shifting Window.
		 */
		this.totalShiftNum = (paddedSampleLen - this.windowSampNum) / stepSampNum;
	}

	/**
	 * Set the next integer samples for STFT If there is samples to calculate in
	 * the net place, this method return True, if not, return False <br>
	 * 
	 * @return {@code true} if there is another samples to calculate,
	 *         {@code false} otherwise
	 * 
	 */
	@Override
	public boolean next() {
		if (shiftIx < totalShiftNum) {
			int offset = stepSampNum * shiftIx;
			// int [] cuttedSamples = new int[FFT_N];
			System.arraycopy(this.totalSamples, offset, tempSamples, 0, windowSampNum);
			shiftIx++;

			this.running = true;
		} else {
			this.running = false;
		}

		return running;
	}

	/**
	 * Applying pre-emphasis to samples
	 * 
	 * @param int
	 *            [] samples
	 * @return double [] pre-emphasized samples
	 */
	private double[] doPreEmph(int[] samples) {
		double[] coef = { 1.0, -0.97 };
//		FirFilter fir = new FirFilter(coef);
//		preEmphSamp = fir.doFirFilter(samples);

		return preEmphSamp;
	}

	/**
	 * Applying Hanning Windowing
	 * 
	 * @param double
	 *            [] pre-emphasied samples
	 * @return double [] windowed samples
	 */
	private double[] windowFrames(double[] preEmphedSamples) {
		windowedSamp = new double[FFT_N];
		
		if (WindowType.HANNING.equals(this.windowType)) {
			for (int n = 0; n < FFT_N; n++) {
				windowedSamp[n] = (0.5 - (0.5 * Math.cos(2 * Math.PI * n / (FFT_N - 1)))) * preEmphedSamples[n];
			}
		}
		return windowedSamp;
	}

	/**
	 * Exexute FFT to samples
	 * 
	 * @param double
	 *            [] indowedSamp
	 * @return double [] fft
	 */
	private double[] doFft(double[] windowedSamp) {
		FastFourierTransformer fft = new FastFourierTransformer(STANDARD);
		Complex[] fftArr = new Complex[this.FFT_N / 2];
		ampSamples = new double[this.FFT_N / 2];

		fftArr = fft.transform(windowedSamp, FORWARD);
		for (int i = 0; i < FFT_N / 2; i++) {
			// Calculating Amplitude
			ampSamples[i] = Math.sqrt(Math.pow(fftArr[i].getReal(), 2) + Math.pow(fftArr[i].getImaginary(), 2));
		}

		return ampSamples;
	}

	/**
	 * Returning MFCC
	 * 
	 * @param double
	 *            [] powerAmplitudeAamples not {@code null}}
	 * @return double [] mfccArray
	 * 
	 */
	private double[] doMfcc(double[] ampSamples) {
		AcousticContext context = new AcousticContextBuilder()
		.setFftN(this.FFT_N)
		.setHz(this.HZ)
		.setMfccCh(this.MFCC_CH)
		.build();
		MelFilter melFilter = new MelFilter(context);
		double[] mfc = new double[MFCC_CH];
		
		DoubleFrame frame = new DoubleFrame(ampSamples, "sample", 0);
		melFilter.process(frame);
		mfc = frame.get("mfc");

		for (int c = 0; c < MFCC_CH; c++) {
			// Log-transfer Sum of FilteredBanked Amplitude
			mfc[c] = Math.log10(mfc[c]);
		}

		
		//-- discrete cosine transform
//		Dct dct = new Dct(MFCC_CH);
//		double[] cepsArr = dct.transform(mfc);
//		double[] mfccArr = new double[MFCC_CEPS_N];
//		System.arraycopy(cepsArr, 0, mfccArr, 0, MFCC_CEPS_N);
//		return mfccArr;
		return mfc;
	}

	/**
	 * Return FFT value of samples
	 * 
	 * @return double [] fftArr
	 */
	@Override
	public double[] getFft() {
		double[] preAmp = doPreEmph(tempSamples);
		double[] windowedSamp = windowFrames(preAmp);
		double[] fftArr = doFft(windowedSamp);

		return fftArr;
	}

	/**
	 * Return MFCC value of samples
	 * 
	 * @return double [] mfccArr
	 */
	@Override
	public double[] getMfcc() {
		double[] preAmp = doPreEmph(tempSamples);
		double[] windowedSamp = windowFrames(preAmp);
		double[] powAmpArr = new double[windowedSamp.length];
		double[] fftArr = doFft(windowedSamp);

		for (int i = 0; i < fftArr.length; i++) {
			powAmpArr[i] = Math.pow(fftArr[i], 2);
			// powAmpArr[i] = fftArr[i] * fftArr[i];
		}

		double[] mfccArr = doMfcc(powAmpArr);

		return mfccArr;
	}
}
