package info.pinlab.snd.trs;

import java.util.List;

public class LabelTier extends AbstractIntervalTier<String>{

	
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



}
