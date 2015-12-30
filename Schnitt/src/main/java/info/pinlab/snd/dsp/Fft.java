package info.pinlab.snd.dsp;

public class Fft extends AbstractFrameProcessor {
	public static final FEParamInt FFT_N = new FEParamInt("FFT_N", 128, Fft.class); 

	protected int fftN = FFT_N.getValue();
	
	
	public Fft(ParameterSheet context){
		super(context);
	};
	
	@Override
	public String getPredecessorKey() {
		return "win";
	}

	@Override
	public String getKey() {
		return "fft";
	}

	@Override
	public void init() {
		setKey("fft");
		setPredecessorKey("win");
		
	}

	@Override
	public double[] process(double[] arr) {

		
		return null;
	}



}
