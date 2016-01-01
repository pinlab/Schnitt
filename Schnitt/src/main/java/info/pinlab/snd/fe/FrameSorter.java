package info.pinlab.snd.fe;

import java.util.TreeMap;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.DoubleFrameTier;
import info.pinlab.snd.trs.Interval;

/**
 * Sorts frames according to target based on overlap between frame and target interval.
 * 
 * <pre>
 * 1.0   100% of frame overlaps with a true interval
 * 0.45   45% of frame overlaps with a true interval
 * 0.0   frame does not overlap with a true interval 
 * </pre>
 * 
 * @author Gabor Pinter
 *
 */
public class FrameSorter {
	double thresh;
	private final TreeMap<Double, Double> overlaps;
	private final DoubleFrameTier frameTier;
	private final BinaryTier target;
	/**
	 * 
	 * @param frame array of frames to be sorted
	 * @param target a {@link BinaryTier} 
	 */
	public FrameSorter(DoubleFrameTier frames, BinaryTier target){
		this.overlaps = new TreeMap<Double, Double>();
		this.frameTier = frames;
		this.target = target;
		for(Double t : frames.getTimeLabels()){
			overlaps.put(t, null);
		}
	}
	
	
	public FrameSorter setThresh(){
		return this;
	}

	
	public void sort(){
		//TODO: sort!
		for(int i = 0; i < target.size(); i++){
			Interval<Boolean> targBool = target.getIntervalX(i);		
			
		}
		
		
	}
	
	
	
	public static void main(String[] args) {

	}

}
