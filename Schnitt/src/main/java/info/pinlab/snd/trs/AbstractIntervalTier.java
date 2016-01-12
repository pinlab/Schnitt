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
//	final Tier.Type tierType ;

	private boolean isEditable = true;
	private String name = "";
	
	
	public AbstractIntervalTier(){
		points = new TreeMap<Double, T>();
		points.put(0.0d, null);
//		this.tierType = type;
	}
	
//	
//	public Type getTierType(){
//		return tierType;
//	}

	
	public void put(Double t, T data){
		points.put(t, data);
	}
	
	public T get(Double t){
		return points.get(t);
	}

	public void clear(){
		points.clear();
	}
	
	
	
	private static final double ROUND_BASE = 1_000_000_000.0d;
	/**
	 * Rounds to the nearest nanosecond (10E-9)
	 * @param sec  time in second
	 * @return rounded time in second
	 */
	public static double roundNanoSec(double sec){
		return Math.round(sec * ROUND_BASE)/ROUND_BASE;
	}


	private Double iteratorIx = null;
	
	@Override
	public Iterator<Interval<T>> iterator(){
		iteratorIx = points.firstKey();
		return this;
	}
	
	
	@Override
	public boolean hasNext() {
		if(iteratorIx==null)
			return false;
		if(points.get(iteratorIx)==null)
			return false;
		return true;
	}

	@Override
	public Interval<T> next(){
		Double endT = points.higherKey(iteratorIx);
		T val = points.get(iteratorIx);
		Interval<T> interval = new Interval<>(iteratorIx, endT, val);
		//-- go to the next one
		iteratorIx = endT;
		return interval;
	}

	@Override
	public void remove() {
		
	}
	
	
	@Override
	public boolean isEditable(){
		return isEditable;
	}
	
	@Override
	public void isEditable(boolean b){
		this.isEditable = b;
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


	
	public Entry<Double, T> getCeil(Double t){
		return  points.ceilingEntry(t);
	}
	
	
	@Override
	public IntervalTier<T> addInterval(Interval<T> interval) {
		return addInterval(interval.startT, interval.endT, interval.label);
	}


	
		
	@Override
	public double getDuration(){
		return points.lastKey();
	}


	
	public Double getFirstInterval(){
		return points.firstKey();
	}
	
	
	public List<Double> getTimeLabels(){
		return new ArrayList<Double>(points.keySet());
	}
		
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		double prev= -1;
		T prevLab = null;
		for(double pt : points.keySet()){
			if(pt > 0.0d){
				sb	.append(prev).append(" - ")
					.append(pt).append(" ")
					.append(prevLab)
					.append('\n');
			}
			prev = pt;
			prevLab = points.get(pt);
		}
		return sb.toString();
		
	}

	public String toStringDebug(){
		StringBuffer sb = new StringBuffer();
		for(Double d : points.keySet()){
			String line = String.format("%3.4f\t%s", d, points.get(d));
			sb.append(line).append("\n");
		}
		return sb.toString();
	}
}
