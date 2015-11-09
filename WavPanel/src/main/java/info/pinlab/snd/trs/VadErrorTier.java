package info.pinlab.snd.trs;

import java.util.List;
import java.util.TreeMap;

import info.pinlab.snd.vad.VadError;

/**
 * 
 * Holds error data for tiers.
 * 
 * @author Gabor PINTER
 *
 */
public class VadErrorTier extends AbstractIntervalTier<VadError> {
	BinaryTier target = null;
	BinaryTier hypo = null;
	
	private static final int TARGET_ON  = 1;
	private static final int TARGET_OFF = 2;
	private static final int HYPO_ON    = 3;
	private static final int HYPO_OFF   = 4;
	private static final int BOTH_ON    = 5;
	private static final int BOTH_OFF   = 6;
	private static final int TARG_ON_HYP_OFF  = 7;
	private static final int TARG_OFF_HYP_ON  = 8;
	
	
	public <T> VadErrorTier(BinaryTier target, BinaryTier hypo){
		//-- conflate tiers
		
		this.target = target;
		this.hypo = hypo;
		
		TreeMap<Double, Integer> conflatedTier = new TreeMap<Double, Integer>(); 
		
		Interval<Boolean> inter=null; 
		for(int i = 0; i < target.size();i++){
			inter = target.getIntervalX(i);
			conflatedTier.put(inter.startT, inter.label ? TARGET_ON : TARGET_OFF);
		}
//		if(inter!=null){
//			conflatedTier.put(inter.endT, TARGET_OFF);
//		}
		
		
		for(int i = 0; i < hypo.size() ;i++){
			inter = hypo.getIntervalX(i);
			Integer targetPt = conflatedTier.get(inter.startT);
			if(targetPt==null){
				conflatedTier.put(inter.startT, inter.label ? HYPO_ON : HYPO_OFF);
			}else{
				switch (targetPt) {
				case TARGET_ON:
					conflatedTier.put(inter.startT, inter.label ? BOTH_ON : TARG_ON_HYP_OFF);
					break;
				case TARGET_OFF:
					conflatedTier.put(inter.startT, inter.label ? TARG_OFF_HYP_ON : BOTH_OFF);
					break;
				default:
					break;
				}
			}
		}
		for(Double key: conflatedTier.keySet()){
			System.out.println(key +"\t" + conflatedTier.get(key));
		}
	}
	
	public void setTargetTier(BinaryTier tier){
		this.target = tier;
	}
	public void setHypoTier(BinaryTier tier){
		this.hypo = tier;
	}
	
	
	
	/**
	 * 
	 */
	public void calculateError(){
		
	}
	
	@Override
	public VadError combineLabels(List<VadError> labels) {
		return null;
	}

	
	public static void main(String[] args){
		BinaryTier t1 = new BinaryTier();
		BinaryTier t2 = new BinaryTier();
		
		
		t1.addInterval(0.0d, 1.0, false);
		t1.addInterval(1.0d, 2.0, true);
		t1.addInterval(2.0d, 3.0, false);
		
		t2.addInterval(0.0d, 0.9, false);
		t2.addInterval(0.9d, 2.2, true);
		t2.addInterval(2.1d, 3.0, false);
		
		System.out.println(t1);
		System.out.println("\n");
		System.out.println(t2);
		
		
		new VadErrorTier(t1, t2);
		
	}
}
