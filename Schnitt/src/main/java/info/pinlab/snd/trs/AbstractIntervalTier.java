package info.pinlab.snd.trs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Mother of tiers.
 * 
 * @author Gabor Pinter
 *
 * @param <T> units of the tier. For example {@link Interval}.
 */
public abstract class AbstractIntervalTier<T> implements IntervalTier<T>{
	public static Logger LOG = LoggerFactory.getLogger(AbstractIntervalTier.class);
	
	final TreeMap<Double, T> points;
	final Tier.Type tierType ;
	
	
	private String name = "";
	
	
	public AbstractIntervalTier(Type type) {
		points = new TreeMap<Double, T>();
		points.put(0.0d, null);
		this.tierType = type;
	}
	
	
	
	public Type getTierType(){
		return tierType;
	}

	

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Interval<T> next() {
		return null;
	}

	@Override
	public void remove() {
		
	}
	

	
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	

	@Override
	public int size(){
		int sz = points.size();
		return sz <= 0 ? 0 : sz-1;
	}
	
	
	@Override
	public Interval<T> getIntervalX(int x){
		 Set<Entry<Double, T>> entries = points.entrySet();
		 if(x > entries.size()){
			 LOG.warn("No tier index {}, tier size: {}", x, points.size());
			 return null;
		 }
		 
		 Iterator<Entry<Double, T>> it = entries.iterator();
		 for(int i = 0 ; i < x ; i++){
			 it.next();
		 }
		 if(it.hasNext()){
			 Entry<Double, T> timeLabelPair = it.next();
			 Double endT = null;
			 if(it.hasNext()){
				 endT = it.next().getKey();
			 }else{
				 endT = null;
			 }
			 return new Interval<T>(timeLabelPair.getKey(), endT, timeLabelPair.getValue());
		 }else{
			 return null;
		 }
	}


	
//	@Override
//	public Double [] getTs(){
//		Double [] timeStamps = new Double[points.size()/2];
//		points.keySet().toArray(timeStamps);
//		return timeStamps;
//	}
	
	
	
	public Entry<Double, T> getCeil(Double t){
		return  points.ceilingEntry(t);
	}
	
	
	
	@Override
	public IntervalTier<T> addInterval(Interval<T> interval) {
		return addInterval(interval.startT, interval.endT, interval.label);
	}
	
	@Override
	public IntervalTier<T> addInterval(double from, double to, T label){
		LOG.trace("Adding interval {}-{} '{}'", from, to, label);
		
		Double floor = points.floorKey(from);
		if(floor==null){ //-- no earlier interval
			points.put(from, label);
		}else{
			//--check if 
			if(floor.equals(from)){
				//-- interval with same start T
				//--   present:  ------++++++++++-----
				//--   new:      ------+++++??-----
				
				ArrayList<T> labels = new ArrayList<T>();
				labels.add(label);
				labels.add(points.get(from));
				label = combineLabels(labels);
				points.put(from, label);
			}else{
				//-- interval 
				//--   present:  ---|++++++++?????????
				//--   new:             |bbbbbb|
				//--   present:  ---|+++|bbbbbb|------
				points.put(from, label);
			}
		}
		
		Double ceiling = points.higherKey(from);
		if(ceiling==null || ceiling > to){ //-- no later interval value
			//--   present:  -----------------++++-----
			//--   adding:         +++++
			//--   adding:   ------+++++------++++-----
			points.put(from, label);
			points.put(to, null);
		}else{
			//--   present:  --------++--+++-+++------
			//--   adding:         +++++++++++
			
			List<T> lablesToCombine = new ArrayList<T>();
			List<Double> marksToDelete = new ArrayList<Double>();
			lablesToCombine.add(label);
			while(ceiling != null && ceiling < to ){
				marksToDelete.add(ceiling);
				T ceilingLabel = points.get(ceiling);
				lablesToCombine.add(ceilingLabel);
				ceiling = points.higherKey(ceiling);
			}
			points.put(from, this.combineLabels(lablesToCombine));
			if(ceiling != null){
				//--   present:  ------------|aaaaaaaaaa|-----
				//--   adding:         |bbbbbbbbb|
				//--   result:   ------|bbbbbbbbb|aaaaaa|-----
				
				T  bridgingLabel =  points.floorEntry(ceiling).getValue();
				points.put(to, bridgingLabel);
			}else{ //-- no ceiling
				points.put(to, null);
			}
			
			for(Double mark : marksToDelete){
				points.remove(mark);
			}
		}
		return this;
	}
	
	
		
	@Override
	public double getDuration(){
		return points.lastKey();
	}

	
		
		
	

}
