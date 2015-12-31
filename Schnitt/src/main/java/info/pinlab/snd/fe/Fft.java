package info.pinlab.snd.fe;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Fft extends AbstractFrameProcessor {
	public static final FEParamInt FFT_N = new FEParamInt("FFT_N", 128, Fft.class);
	
	
	private int fftN = FFT_N.get();
	private int nyqFreq;
	
	private FastFourierTransformer fftTransformer;
	private double [] ampArr;
	private Complex[] fftArr;
	


	public Fft(ParameterSheet context){
		super(context);
	}
	

	public double getAmplitude(Complex i){
		// Calc. Amplitude
		return Math.sqrt(Math.pow(i.getReal(), 2) + Math.pow(i.getImaginary(), 2));
	}
	
	@Override
	public void init() {
		fftN = context.get(FFT_N);
		nyqFreq = fftN/2;
		fftTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
		fftArr = new Complex [fftN];
		ampArr = new double [nyqFreq];
	}

	@Override
	public double[] process(double[] arr) {
		fftArr = fftTransformer.transform(arr, TransformType.FORWARD);
		for(int i = 0; i<nyqFreq; i++){
			// Calc. Amplitude
			ampArr[i] = getAmplitude(fftArr[i]);
		}
		return ampArr;
	}
}