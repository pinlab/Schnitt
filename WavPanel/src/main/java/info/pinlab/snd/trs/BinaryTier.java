package info.pinlab.snd.trs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;


/**
 * 
 * Tier of ONs and OFFs. 
 * Useful e.g. for Voice Activity Detector.
 * 
 * <pre>
 * {@code
 * 
 * a)  -----++++++++++++++++--------    (original tier data)
 * b)            ++++++++               (adding a 'true' interval)
 * a+b)-----++++++++++++++++--------    (resulting true/false tier)
 *
 * a)  ----------++-----------------
 * b)       ++++++++++++++++
 * a+b)-----++++++++++++++++--------    
 *
 *
 * a)  ----------+++++++++----------
 * b)       ++++++++++
 * a+b)-----++++++++++++++----------    
 *
 *
 * a)  ---------------+++++++++-----
 * b)       +++++
 * a+b)-----+++++-----+++++++++-----    
 *
 *
 * a)  -----+++++++++++++++++++-----
 * b)          ------                   (adding a 'false' interval)
 * a+b)-----+++------++++++++++------
 *}
 *</pre>
 * @author kinoko
 */
public class BinaryTier extends AbstractIntervalTier<Boolean>{
//	private final TreeMap<Double, Boolean> points ;
	
	
	public BinaryTier(){
		super();
		super.points.put(0.0d, false);
//		points = new TreeMap<Double, Boolean>();
//		points.put(0.0d, false);
	}
	
	
	public double getDuration(){
		return points.lastKey();
	}

	
	
//	public void addInterval(double from, double to){
//		if(to<from){ //-- switch if wrong order
//			double tmp = from;
//			from = to;
//			to = tmp; 
//		}
//		if(from < this.getTierStartTime()){
//			this.setTierStartTime(from);
//		}
//		addInterval(from, to, true);
//	}

	
	
	
	
	@Override
	public void addInterval(Interval<Boolean> interval){
		addInterval(interval.startT, interval.endT, (boolean)(interval.label==null? false: interval.label));
	}

	
/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 */
	@Override
	public void addInterval(double from, double to, Boolean b){
		System.out.println(from +" - " + to);
		if(from > to){// wrong order! switch them
			LOG.trace("Wrong timestamps order {}-{} ! Switching!", from, to);
			from  = from + to;
			to    = from - to;
			from  = from - to;
		}
		
		if(b==null)b = false;
		//-- remove in-between values:
		List<Double> toDelete = new ArrayList<Double>();
		Double higherKey = points.higherKey(from);
		if(higherKey==null){
			if(points.containsKey(from)){
				higherKey = from;
			}
		}
		if(higherKey==null){
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
		
		
		while(higherKey!=null && higherKey <= to){
//			System.out.println(val + "\t" + to + " " + (val<=to));
			toDelete.add(higherKey);
			higherKey = points.higherKey(higherKey); 
		}
		for(Double delValue : toDelete){
//			System.out.println("DEL " + toDelete);
			points.remove(delValue);
		}
		

		Entry<Double, Boolean> floor = points.floorEntry(from);
		if(floor!=null && floor.getValue()==b){
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
	

	public String debugPrint(){
		StringBuffer sb = new StringBuffer();
		
		double prev= -1;
		boolean prevLab = false;
		for(double pt : super.points.keySet()){
			if(pt > 0.0d){
				sb	.append(prev).append(" - ")
					.append(pt).append(" ")
					.append(prevLab)
					.append('\n');
			}
			prev = pt;
			prevLab = super.points.get(pt);
		}
		return sb.toString();
	}


	@Override
	public Boolean combineLabels(List<Boolean> labels) {
		return labels.get(0);
	}
	
	
	
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		double prev= -1;
		Boolean prevLab = false;
		for(double pt : super.points.keySet()){
			if(pt > 0.0d){
				sb	.append(prev).append(" - ")
					.append(pt).append(" ")
					.append(prevLab)
					.append('\n');
			}
			prev = pt;
			prevLab = super.points.get(pt);
		}
		return sb.toString();
	}
	
	
	
	public static void main(String[] args) {

		BinaryTier tier = new BinaryTier();
		tier.addInterval(0.15f, 0.2f, true);
		tier.addInterval(0.25f, 0.8f,  true);
		tier.addInterval(0.5f, 1.5f,  true);
		
//		tier.addInterval(0.1d, 0.3d, true);
//		tier.addInterval(0.2f, 0.5f, false);
//		tier.addInterval(0.1d, 0.3d, false);


//		tier.addInterval(1.0d, 2.0d);
//		tier.addInterval(2.0d, 4.0d);
//		tier.addInterval(4.0d, 5.0d);
		
		tier.debugPrint();
		
	}


}









