package info.pinlab.snd.fe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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
	//	private final DoubleFrameTier frameTier;
	private final BinaryTier targetTier;
	private final double frameLenInSec;
	private final double frameShiftInSec;
	private final List<Double> frameTimes;

	/**
	 * 
	 * @param frame array of frames to be sorted
	 * @param target a {@link BinaryTier} 
	 */
	public FrameSorter(DoubleFrameTier frames, BinaryTier target){
		this.overlaps = new TreeMap<Double, Double>();
		this.targetTier = target;
		this.frameLenInSec = frames.getFrameLenInMs()/1000;
		this.frameShiftInSec = this.frameLenInSec/2;
		frameTimes = new ArrayList<Double>(frames.getTimeLabels());
		for(Double t : frameTimes){
			overlaps.put(t, 0.0d);
		}
	}


	public FrameSorter setThresh(){
		return this;
	}


	public void sort(){
		int ix = 0;
		Interval<Boolean> interval;
		Boolean lastInterval = false;
		double overlap = 0.0;
		OUTER: for(int i = 0; i < targetTier.size(); i++){
			interval = targetTier.getIntervalX(i);
			if(!interval.label) continue OUTER;
			ix = (int)Math.floor((interval.startT-frameLenInSec)/frameLenInSec);
			ix = ix < 0 ? 0 : ix;
			
			
			double frameStart = frameTimes.get(ix);
			while(frameStart < interval.endT){
				double frameEnd = frameStart + frameLenInSec;
				overlap = overlaps.get(frameStart);
				if(frameStart <= interval.startT && frameEnd >= interval.startT){
					//-- if: overarching 
					if(frameEnd < interval.endT){
						overlap += (frameEnd-interval.startT)/frameLenInSec;
					}else{
						overlap += (interval.endT-interval.startT)/frameLenInSec;
					}
				}
				if(frameStart > interval.startT){
					if(frameEnd < interval.endT){
						overlap += 1.0;
					}else{
						overlap += (interval.endT-frameStart)/frameLenInSec;
					}
				}
				overlaps.put(frameStart, overlap);
				ix++;
				frameStart = frameTimes.get(ix);
			}
			//TODO: find way to reset ix to some previous to interval..
		}
	}


	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(Double t : overlaps.keySet()){
			sb.append(t).append("\t").append(overlaps.get(t)).append("\n");
		}
		return sb.toString();
	}


	public static void main(String[] args) {

	}

}
