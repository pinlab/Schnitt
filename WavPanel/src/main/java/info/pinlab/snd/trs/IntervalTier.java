package info.pinlab.snd.trs;

import java.util.Iterator;
import java.util.List;


/**
 * 
 * Holds intervals. 
 * 
 * @author Gabor Pinter
 *
 * @param <T>
 */
public interface IntervalTier<T> extends Tier, Iterator<Interval<T>>{

	
	public Interval<T> getIntervalX(int x);
	
	public IntervalTier<T> addInterval(double from, double to, T label);
	public IntervalTier<T> addInterval(Interval<T> interval);

	/**
	 * Concat operation to be implemented for type T. 
	 * For example, for Strings, simple concat (e.g., by StringBuffer) 
	 * 
	 * @param labels labels of type T to be combined for a merged interval
	 * @return combined label for the merged interval
	 */
	public T combineLabels(List<T> labels);
	
}
