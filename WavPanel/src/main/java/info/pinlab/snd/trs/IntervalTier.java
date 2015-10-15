package info.pinlab.snd.trs;

import java.util.TreeMap;

/**
 * 
 *  Should be thread safe!
 *  to hold intervals -- while being edited
 * 
 * @author kinoko
 *
 */
public class IntervalTier extends AbstractTier{
	
	TreeMap<Double, String> intervals;
	

	public void addPoint(double t){
		addPoint(t, "");
	}
	public void addPoint(double t, String label){
		intervals.put(t, label);
	}
	
	public void removePoint(double at){
		intervals.remove(at);
		//TODD: move label to previous
	}

	public void movePoint(double from, double to){
		String label = intervals.get(from);
		intervals.remove(from);
		intervals.put(to, label);
	}

	
	
//	original:
//	 | a  |  b  |  c  |   d    |
// request start & end points
//     |           |
// returns
//	 | a  |  b  |  c  
	public TreeMap<Double, String> getIntervalsBetween(double t1, double t2){
		TreeMap<Double, String> section = new TreeMap<Double, String>();
		
		double prevTime = 0.0f;
		for(double start : intervals.keySet()){
			if (start > t2){
				break;
			}
			if (start > t1){
				prevTime = start;
				continue;
			}
			section.put(start, intervals.get(start));
		}
		//-- add the first interval
		section.put(prevTime, intervals.get(prevTime));
		
		return section;
	}
	
	
	
	
	
	public void clear(){
		intervals.clear();
//		labels.clear();
	}
	
	
	public static void main(String[] args) {
	}
}
