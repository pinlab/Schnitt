package info.pinlab.snd.dsp;

public class Fft extends AbstractFrameProcessor {

	public Fft(AcousticContext context){
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
//-- 
	
	}

	@Override
	public double[] innerProcess(double[] arr) {
		return null;
	}

}
