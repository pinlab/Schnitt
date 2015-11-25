package info.pinlab.snd.trs;

import java.util.List;


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
		super.points.put(0.0d, null);
//		points = new TreeMap<Double, Boolean>();
//		points.put(0.0d, false);
	}
	
	
	public double getDuration(){
		return points.lastKey()-points.firstKey();
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
	public IntervalTier<Boolean> addInterval(Interval<Boolean> interval){
		return addInterval(interval.startT, interval.endT, (boolean)(interval.label==null? false: interval.label));
	}

	
/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 */
	@Override
	public IntervalTier<Boolean> addInterval(double from, double to, Boolean b){
		if(from > to){// wrong order! switch them
			LOG.trace("Wrong timestamps order {}-{}..switching!", from, to);
			from  = from + to;
			to    = from - to;
			from  = from - to;
		}
		if(b==null)b = false; //-- null means right edge of the last interval -> let's not use it 
		
//		//-- HIGHER side 
		Double leftOfTo = points.floorKey(to);
		if(leftOfTo==null){ //-- no left point...
			points.put(to, null);
		}else{ // higher than 'to'
			Boolean leftOfToVal = points.get(leftOfTo);
			if(leftOfTo >= from){
				if(leftOfToVal==null){ //-- end before 'to'
					points.remove(leftOfTo);
					points.put(to, null);
				}else{ //-- leftOfToVal has a value
					if(leftOfToVal==b){//-- same as b
						//-- do nothing!
						points.remove(to);
					}else{
						points.put(to, leftOfToVal);
					}
				}
			}else{ //-- 
				if(leftOfToVal!=null){ //-- inserting into an interval
					if(leftOfToVal==b){ //-- same interval
						//-- don't add this interval
						return this;
					}else{
						points.put(to, leftOfToVal);
					}
				}else{
					points.put(to,null);
				}
			}
		}//-- leftOfTo > 'to'

		
		Double lower = points.lowerKey(from);
		if(lower==null){ //-- 'from' is lower than anything
			points.put(from, b);
		}else{
			Boolean lowerVal = points.get(lower);
			if(lowerVal==null){ //-- 'from' is after last interval
				Double lower2 = points.lowerKey(lower);
				if(lower2==null){
					if(b){ //-- adding 'true'
						points.put(lower, false);
						points.put(from, true);
					}else{
						//-- don't add
						points.put(lower, false);
					}
				}else{
					lowerVal = points.get(lower2);
					if(lowerVal==null) lowerVal=false;
					if(lowerVal && b){ //-- both 'true'
						// tier: ++++|
						// add :         |+++
						//     : ++++|---|+++
						points.put(lower, false);
						points.put(from, true);
					}
					if(lowerVal && !b){  
						// tier: ++++|
						// add :         |---
						//     : ++++|-------
						points.put(lower, false);
						//dont'add  'from'.'false'
					}
					if(!lowerVal && b){  
						// tier: ----|
						// add :         |+++
						//     : --------|+++
						points.remove(lower);
						points.put(from,  true);
					}
					if(!lowerVal && !b){  
						// tier: ----|
						// add :         |---
						//     : ------------
						points.remove(lower);
					}
				}
			}else{ // lowerVal!=null 'lower' before 'from' 
				if(lowerVal == b ){ 
					//-- don't add interval if it is the same as 'lower's
					points.remove(from);
				}else{
					points.put(from, b);
				}
			}
		}		
//		
		
		Double pt = points.higherKey(from);
		while(pt!=null && pt < to){
			points.remove(pt);
			pt=points.higherKey(pt);;
		}
		return this;
	}
	
	/**
	 * Clears all intervals
	 */
	public void clear(){
		points.clear();
	}
	

	public String debugPrint(){
		StringBuffer sb = new StringBuffer();
		
		for(Double d : super.points.keySet()){
			String line = String.format("%3.4f\t%s", d, super.points.get(d));
			sb.append(line).append("\n");
		}
		return sb.toString();
//		double prev= -1;
//		boolean prevLab = false;
//		for(double pt : super.points.keySet()){
//			if(pt > 0.0d){
//				sb	.append(prev).append(" - ")
//					.append(pt).append(" ")
//					.append(prevLab)
//					.append('\n');
//			}
//			prev = pt;
//			prevLab = super.points.get(pt);
//		}
//		return sb.toString();
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









