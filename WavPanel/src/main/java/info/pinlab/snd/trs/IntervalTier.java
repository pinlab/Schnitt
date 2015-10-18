package info.pinlab.snd.trs;

import java.util.Iterator;
import java.util.List;

public interface IntervalTier<T> extends Tier, Iterator<Interval<T>>{

	
	public Interval<T> getIntervalX(int x);
	
	public void addInterval(double from, double to, T label);
	public void addInterval(Interval<T> interval);

	/**
	 * Concat operation to be implemented for type T. 
	 * For example, for Strings, simple concat (e.g., by StringBuffer) 
	 * 
	 * @param labels labels of type T to be combined for a merged interval
	 * @return combined label for the merged interval
	 */
	public T combineLabels(List<T> labels);
	
}
