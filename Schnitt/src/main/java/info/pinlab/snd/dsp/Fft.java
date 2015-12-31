package info.pinlab.snd.dsp;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import info.pinlab.snd.dsp.ParameterSheet.ProcessorParameter;

public class Fft extends AbstractFrameProcessor {
	@ProcessorParam(label="FFT_N")
	public static final int FFT_N = 128;
	
	@ParamInt(label="FFT_N")
	protected int fftN = 128;
	
	Complex [] fftArr;
	double[] ampArr;
	FastFourierTransformer fftTransformer;
	
	enum FftParam implements ProcessorParameter{
		FFT_N(128),
		DO_MAG_SPEC(true),
		DO_POW_SPEC(true),
		DO_LOGPOW_SPEC(true),
		;

		final private String id;
		final private String label;
		private Object value;
		
		FftParam(Object val){
			value = val;
			label =  this.name().toUpperCase();
			id = FftParam.class.getName() + "." + label ;
		}
		@Override
		public String getUniqName() {
			return id; 
		}
		@Override
		public String getLabel() {
			return null;
		}
		
		@Override
		public boolean getBoolean() {
			return (boolean) value;
		}
		@Override
		public int getInt() {
			return (int) value;
		}
		@Override
		public double getDouble() {
			return (double)value;
		}
		@Override
		public Object get() {
			return value;
		}	
	}

	public static ProcessorParameter[] getProcessorParams(){
		return FftParam.values();
	}
	
	
	public Fft(ParameterSheet context){
		super(context);
	}
	
	@Override
	public String getPredecessorKey() {
		return "win";
	}

	@Override
	public String getKey() {
		return "fft";
	}


	public double getAmplitude(Complex i){
		// Calc. Amplitude
		return Math.sqrt(Math.pow(i.getReal(), 2) + Math.pow(i.getImaginary(), 2));
	}
	
	@Override
	public void init() {
		setKey("fft");
		setPredecessorKey("win");
		fftArr = new Complex [fftN];
		ampArr = new double [fftN/2];
		fftTransformer = new FastFourierTransformer(DftNormalization.STANDARD);	
	}

	@Override
	public double[] process(double[] arr) {
		fftArr = fftTransformer.transform(arr, TransformType.FORWARD);
		for(int i = 0; i<ampArr.length; i++){
			// Calc. Amplitude
			ampArr[i] = getAmplitude(fftArr[i]);
		}
		return ampArr;
	}
}
