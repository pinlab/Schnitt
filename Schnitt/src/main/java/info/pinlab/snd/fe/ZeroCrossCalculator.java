package info.pinlab.snd.fe;

/**
 * Calculates zero crossing rate.
 * Number of crossing zero, normalized by array length.
 * 
 * @author Gabor Pinter
 *
 */
public class ZeroCrossCalculator extends AbstractFrameProcessor {

	public ZeroCrossCalculator(ParamSheet context) {
		super(context);
	}
	public ZeroCrossCalculator() {
		super(null);
	}

	
	@Override
	public void init() { /* pass */	}

	
	@Override
	public double[] process(double[] arr){
		int crossX = 0; 
		
		boolean prevIsPos = arr[0] > 0;
		for(double d : arr){
			boolean isPos = d > 0;
			if(prevIsPos^isPos) crossX++;
			prevIsPos = isPos;
		}
		return new double[]{(crossX / (double)arr.length)};
	}

	

}
