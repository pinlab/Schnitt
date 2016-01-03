package info.pinlab.snd.fe;


/**
 * Calculates power in the frame.
 * Normalization is by number of samples.
 * 
 * @author Gabor Pinter
 *
 */
public class PowerCalculator extends AbstractFrameProcessor {

	public PowerCalculator(ParamSheet context) {
		super(context);
		super.setKey("intensity");
	}

	@Override
	public void init() {
		//-- pass
	}

	@Override
	public double[] process(double[] arr) {
		double len = arr.length;
		double sum = 0;
		for(int i = 0 ;i < arr.length ; i++){
			sum += arr[i]*arr[i] ;
		}
		return new double[]{sum/len};
	}

}
