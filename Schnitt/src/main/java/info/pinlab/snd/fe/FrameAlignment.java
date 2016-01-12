package info.pinlab.snd.fe;

import java.util.ArrayList;
import java.util.List;
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
public class FrameAlignment {
	private final TreeMap<Double, Double> overlaps;
	//	private final DoubleFrameTier frameTier;
	private final BinaryTier targetTier;
	private final double frameLenInSec;
	private final List<Double> frameTimes;
	private final DoubleFrameTier tier; 
	

	/**
	 * 
	 * @param frame array of frames to be sorted
	 * @param target a {@link BinaryTier} 
	 */
	public FrameAlignment(DoubleFrameTier frames, BinaryTier target){
		this.overlaps = new TreeMap<Double, Double>();
		this.targetTier = target;
		this.frameLenInSec = frames.getFrameLenInSec();
		frameTimes = new ArrayList<Double>(frames.getTimeLabels());
		for(Double t : frameTimes){
			overlaps.put(t, 0.0d);
		}
		tier = frames;
	}

	
	public TreeMap<Double, Double> calcOverlap(){
		return calcOverlap("overlap");
	}
	

	/**
	 * Calculates the ovelaps of the frames with ON/TRUE sections of the {@link BinaryTier}. 
	 * 
	 * @param featureKey the map key for the overlap value in the {@link DoubleFrame}
	 * @return a TreeMap with time-overlap% entries
	 */
	public TreeMap<Double, Double> calcOverlap(String featureKey){
		int ix = 0;
		Interval<Boolean> interval;
		double overlap = 0.0;
		OUTER: for(int i = 0; i < targetTier.size(); i++){
			interval = targetTier.getIntervalX(i);
			if(!interval.label) continue OUTER;
			ix = (int)Math.floor((interval.startT-frameLenInSec)/frameLenInSec);
			ix = ix < 0 ? 0 : ix;
			
			double frameStart = frameTimes.get(ix);
//			System.out.println( interval.startT + "  ?  " +  (frameStart+frameLenInSec));
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
		}
		
		for(int i = 0; i < overlaps.size(); i++){
			Double t = frameTimes.get(i);
			DoubleFrame frame = tier.getFrameAt(t);
			frame.addArray(featureKey, new double[]{overlaps.get(t)});
			frame.addNumber(featureKey, overlaps.get(t));
		}
		return overlaps;
	}
	
	

	public String debugPrint(){
		StringBuffer sb = new StringBuffer();
		for(Double t : overlaps.keySet()){
			String start = String.format("%.3f", Math.round(t*1000)/1000.0d); 
			String end = String.format("%.3f", Math.round((t +frameLenInSec)*1000)/1000.0d);
			String overlap = String.format("%.3f", Math.round(1000*overlaps.get(t))/1000.0d );
			sb.append(start).append(" - ").append(end)
			.append("\t")
			.append(overlap).append("\n");
		}
		return sb.toString();
	}


	public static void main(String[] args) {

	}

}
