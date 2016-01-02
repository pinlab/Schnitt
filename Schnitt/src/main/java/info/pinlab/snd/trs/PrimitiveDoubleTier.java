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
	
	
	public PrimitiveDoubleTier(double [] pts){
		this.points = new double[pts.length];
		System.arraycopy(pts, 0, this.points, 0, pts.length);
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



	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void isEditable(boolean b) {
		// TODO Auto-generated method stub
		
	}


}
