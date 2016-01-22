package info.pinlab.snd.fe;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Fft extends AbstractFrameProcessor {
	public static final FEParamInt FFT_N = new FEParamInt("FFT_N", 512, Fft.class);
	public static final FEParamInt FFT_SIGNAL_LEN = new FEParamInt("FFT_SIGNAL_LEN", 320, Fft.class);
	
	
	private int fftN = FFT_N.getValue();
	private int sigSize ;
	private int sigSizePow2 ;
	double normfact;
	private int nyqFreq;
	
	private FastFourierTransformer fftTransformer;


	public Fft(ParamSheet context){
		super(context);
	}

	public Fft(int fftN, int signalSize){
		super(null);
		this.fftN = fftN;
		this.sigSize = signalSize;
		init();
	}
	
	

	private final static double getAmplitude(Complex i){
		// Calc. Amplitude
		return Math.sqrt(Math.pow(i.getReal(), 2) + Math.pow(i.getImaginary(), 2));
	}
	
	@Override
	public void init() {
		super.setKey("fft");
		if(context!=null){
			fftN = context.get(FFT_N);
			sigSize = context.get(FFT_SIGNAL_LEN);
		}
		sigSizePow2 = (int) Math.pow(2, 
				Math.ceil(Math.log(sigSize)/Math.log(2)));
		nyqFreq = fftN/2;
		normfact = 1.4142135623730951 / fftN;
		
		fftTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
	}

	@Override
	public double[] process(double[] samples) {
		//-- padding
		//double [] arr = Arrays.copyOf(samples, sigSizePow2);
		double [] arr = Arrays.copyOf(samples, fftN);
		Complex [] fft = fftTransformer.transform(arr, TransformType.FORWARD);
		double [] mag = new double[nyqFreq];
		for(int i = 0; i<nyqFreq; i++){
			// Calc. Amplitude
			mag[i] = normfact * getAmplitude(fft[i]);
//			ampArr[i] = 1.0 / (fftN*
//			(Math.pow(fftArr[i].getReal(), 2) + Math.pow(fftArr[i].getImaginary(), 2))
//			;
		}
		return mag;
	}
}