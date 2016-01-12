package info.pinlab.snd.trs;

import java.util.List;

public class LabelTier extends AbstractIntervalTier<String>{

	
	public LabelTier() {
	}



	@Override
	public String combineLabels(List<String> labels){
		
		StringBuilder sb = new StringBuilder();
		for(String label: labels){
			sb.append(" ");
			sb.append((label==null ? "" : label));
		}
		return sb.toString().trim();
	}
	
//	public void debugPrint(){
//		for(Double marker : super.points.keySet()){
//			System.out.println(marker + "\t" + super.points.get(marker));
//		}
//	}

	@Override
	public IntervalTier<String> addInterval(double from, double to, String label) {
		if(from > to){// wrong order! switch them
			LOG.trace("Wrong timestamps order {}-{}..switching!", from, to);
			from  = from + to;
			to    = from - to;
			from  = from - to;
		}
		from = super.roundNanoSec(from);
		to = super.roundNanoSec(to);
		

		//-- "null" -> "" 
		Double leftOfFrom = points.floorKey(from);
		if(leftOfFrom!=null){ //-- there is a value!
			if(points.get(leftOfFrom)==null){ //-- this was the las point...
				points.put(leftOfFrom, "");
			}
		}
		
		points.put(from, label);
		//-- add "null" or empty "" as closing label
		points.put(to, points.ceilingKey(to)==null /* no pt */ ? null : "");
		
		//-- remove points in between:
		Double pt = points.higherKey(from);
		while(pt!=null && pt < to){
			points.remove(pt);
			pt=points.higherKey(pt);
		}
		return this;
	}


	


}
