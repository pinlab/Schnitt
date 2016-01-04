package info.pinlab.snd.fe;

/**
 * Calculates power in the frame.
 * Normalization is by number of samples.
 * 
 * @author Gabor Pinter
 *
 */
public class PowerCalculator extends AbstractFrameProcessor {
	
//	public static final FEParamString HANDLE = new FEParamString(PowerCalculator.class.getName() + ".HANDLE", "pow", PowerCalculator.class); 
//	public static final FEParamDouble POW_THRESH = new FEParamDouble("POW_THRESH", 0.5, PowerCalculator.class);
	
	public PowerCalculator(ParamSheet context) {
		super(context);
		super.setKey("power");
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
