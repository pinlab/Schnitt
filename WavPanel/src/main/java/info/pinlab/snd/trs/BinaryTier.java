package info.pinlab.snd.trs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * 
 * Tier of ONs and OFFs. 
 * Useful e.g. for Voice Activity Detector.
 * 
 * @author kinoko
 *
 *
 * a)  -----++++++++++++++++--------
 * b)            ++++++++
 * a+b)-----++++++++++++++++--------    (like binary addition)
 *
 * a)  ----------++++++++-----------
 * b)       ++++++++++++++++
 * a+b)-----++++++++++++++++--------    (like binary addition)
 *
 *
 * a)  ----------+++++++++----------
 * b)       ++++++++++
 * a+b)-----++++++++++++++----------    (like binary addition)
 *
 *
 * a)  ---------------+++++++++-----
 * b)       +++++
 * a+b)-----+++++-----+++++++++-----    (like binary addition)
 *
 *
 * a)  -----+++++++++++++++++++-----
 * b)          ------
 * a+b)-----+++------++++++++++------
 *
 *
 */
public class BinaryTier extends AbstractTier{
	TreeMap<Double, Boolean> points ;
	
	
	public BinaryTier(){
		points = new TreeMap<Double, Boolean>();
		points.put(0.0d, false);
	}
	
	

	public void addInterval(double from, double to){
		if(to<from){ //-- switch if wrong order
			double tmp = from;
			from = to;
			to = tmp; 
		}
		if(from < this.getTierStartTime()){
			this.setTierStartTime(from);
		}
		addInterval(from, to, true);
	}
		
	
	
/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 */
	public void addInterval(double from, double to, boolean b){
		//-- remove in-between values:
		List<Double> toDelete = new ArrayList<Double>();
		Double val = points.higherKey(from);
		if(val==null){
			if(points.containsKey(from)){
				val = from;
			}
		}
		if(val==null){
			if(b){
				Entry<Double, Boolean> floor = points.floorEntry(from);
				if(points.containsKey(from)){
					floor = points.lowerEntry(from);
				}
				if(floor!=null){
//					System.out.println(floor.getKey());
					if(floor.getValue()){// if it's true!
						//-- don't put from!
					}else{
						points.put(from, b);
					}
				}
				points.put(to, !b);
			}
			return;
		}
		
		
		while(val!=null && val <= to){
//			System.out.println(val + "\t" + to + " " + (val<=to));
			toDelete.add(val);
			val = points.higherKey(val); 
		}
		for(Double delValue : toDelete){
//			System.out.println("DEL " + toDelete);
			points.remove(delValue);
		}
		

		Entry<Double, Boolean> floor = points.floorEntry(from);
		if(floor.getValue()==b){
			//-- same value -> ignore 'from' points
		}else{
			points.put(from, b);
		}
		
		boolean notB = !b;
		Entry<Double, Boolean> ceil = points.ceilingEntry(to);
		if(ceil==null){
			if(b){
				points.put(to, notB);
			}
		}else{
			if(ceil.getValue()!=notB){
				points.put(to, notB);
			}
		}
	}
	
	/**
	 * Clears all intervals
	 */
	public void clear(){
		points.clear();
	}
	

	public void debugPrint(){
		
		for(Double pt : points.keySet()){
			System.out.println(pt + "\t" + points.get(pt));
		}
		
	}
	
	public static void main(String[] args) {

		BinaryTier tier = new BinaryTier();
//		tier.addInterval(0.15f, 0.2f, true);
//		tier.addInterval(0.25f, 0.8f,  true);
//		tier.addInterval(0.5f, 1.5f,  true);
		
//		tier.addInterval(0.1d, 0.3d, true);
//		tier.addInterval(0.2f, 0.5f, false);
//		tier.addInterval(0.1d, 0.3d, false);


//		tier.addInterval(1.0d, 2.0d);
//		tier.addInterval(2.0d, 4.0d);
//		tier.addInterval(4.0d, 5.0d);
		
		tier.debugPrint();
		
	}
	
}









