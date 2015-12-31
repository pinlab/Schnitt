package info.pinlab.snd.dsp;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import info.pinlab.snd.dsp.ParameterSheet.ProcessorParameter;

public class Fft extends AbstractFrameProcessor {
	private int fftN;
	private int nyqFreq;
	
	private FastFourierTransformer fftTransformer;
	private double [] ampArr;
	private Complex[] fftArr;
	
	enum FftParams implements ProcessorParameter{
		FFTN(128)
		;
		
		final String id;
		final String label;
		final Object defVal;
		FftParams(Object defVal){
			this.id = FrameProducer.class.getName()+"." +this.toString().toUpperCase();
			this.label = id.toUpperCase();
			this.defVal = defVal;
		}

		@Override
		public String getUniqName(){return id;			}
		@Override
		public String getLabel() {	return label;		}
		@Override
		public boolean getBoolean(){return (boolean)defVal;	}
		@Override
		public int getInt() {		return (int)defVal;	}
		@Override
		public double getDouble(){	return (int) defVal;}
		@Override
		public Object get(){		return defVal;		}
	}
	

	public static ProcessorParameter[] getProcessorParams(){
		return FftParams.values();
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
		nyqFreq = fftN/2;
		fftTransformer = new FastFourierTransformer(DftNormalization.STANDARD);	
		fftN = context.getInt(FftParams.FFTN);
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

