package info.pinlab.snd.trs;

/**
 * 
 * Holds double data such as:
 * - probability values
 * - mfcc componants
 * @author kinoko
 *
 */
public class PrimitiveDoubleTier implements PointTier {
	double [] points;
	
	
	public PrimitiveDoubleTier(){
		
	}
	
	
	
	public double[] getPoints(){
		return points;
	}
	
	
	
	@Override
	public int size() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSamplingFreq() {
		// TODO Auto-generated method stub
		return 0;
	}

}
