package info.pinlab.snd.trs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public abstract class AbstractIntervalTier<T> implements IntervalTier<T>{

	TreeMap<Double, T> markers = new TreeMap<Double, T>();
	
	String name = "";
	
	
	


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
		int sz = markers.size();
		return sz <= 0 ? 0 : sz-1;
	}
	
	
	@Override
	public Interval<T> getIntervalX(int x){
		 Set<Entry<Double, T>> entries = markers.entrySet();
		 if(x >= entries.size()){
			 return null;
		 }
		 
		 Iterator<Entry<Double, T>> it = entries.iterator();
		 for(int i = 0 ; i< x ; i++){
			 it.next();
		 }
		 Entry<Double, T> timeLabelPair = it.next();
		 Double endT = null;
		 if(it.hasNext()){
			 endT = it.next().getKey();
		 }else{
			 return null;
		 }
		 return new Interval<>(timeLabelPair.getKey(), endT, timeLabelPair.getValue());
	}


	
	
	
	@Override
	public void addInterval(Interval<T> interval) {
		addInterval(interval.startT, interval.endT, interval.label);
	}
	
	public void addInterval(double from, double to, T label){
		
		Double floor = markers.floorKey(from);
		if(floor==null){ //-- no earlier interval
			markers.put(from, label);
		}else{
			//--check if 
			if(floor.equals(from)){
				//-- interval with same start T
				//--   present:  ------++++++++++-----
				//--   new:      ------+++++??-----
				
				ArrayList<T> labels = new ArrayList<T>();
				labels.add(label);
				labels.add(markers.get(from));
				label = combineLabels(labels);
				markers.put(from, label);
			}else{
				//-- interval 
				//--   present:  ---|++++++++?????????
				//--   new:             |bbbbbb|
				//--   present:  ---|+++|bbbbbb|------
				markers.put(from, label);
			}
		}
		
		Double ceiling = markers.higherKey(from);
		if(ceiling==null || ceiling > to){ //-- no later interval value
			//--   present:  -----------------++++-----
			//--   adding:         +++++
			//--   adding:   ------+++++------++++-----
			markers.put(from, label);
			markers.put(to, null);
		}else{
			//--   present:  --------++--+++-+++------
			//--   adding:         +++++++++++
			
			List<T> lablesToCombine = new ArrayList<T>();
			List<Double> marksToDelete = new ArrayList<Double>();
			lablesToCombine.add(label);
			while(ceiling != null && ceiling < to ){
				marksToDelete.add(ceiling);
				T ceilingLabel = markers.get(ceiling);
				lablesToCombine.add(ceilingLabel);
				ceiling = markers.higherKey(ceiling);
			}
			markers.put(from, this.combineLabels(lablesToCombine));
			if(ceiling != null){
				//--   present:  ------------|aaaaaaaaaa|-----
				//--   adding:         |bbbbbbbbb|
				//--   result:   ------|bbbbbbbbb|aaaaaa|-----
				
				T  bridgingLabel =  markers.floorEntry(ceiling).getValue();
				markers.put(to, bridgingLabel);
			}else{ //-- no ceiling
				markers.put(to, null);
			}
			
			for(Double mark : marksToDelete){
				markers.remove(mark);
			}
			
		}
	}
		
		
	

}
