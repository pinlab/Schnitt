package info.pinlab.snd.trs;

import java.util.List;

public class LabelTier extends AbstractIntervalTier<String>{

	
	public LabelTier() {
		super(Type.NOT_SET);
	}

	public LabelTier(Type type) {
		super(type);
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
	
	
	
	public void debugPrint(){
		for(Double marker : super.points.keySet()){
			System.out.println(marker + "\t" + super.points.get(marker));
		}
	}

	@Override
	public IntervalTier<String> addInterval(double from, double to, String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntervalTier<String> addInterval(Interval<String> interval) {
		// TODO Auto-generated method stub
		return null;
	}



}
