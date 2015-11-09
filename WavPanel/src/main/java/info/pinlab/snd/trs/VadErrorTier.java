package info.pinlab.snd.trs;

import java.util.List;

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
	
	
	public <T> VadErrorTier(BinaryTier target, BinaryTier hypo){
		
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
		
		
//		t1.addInterval(0.0d, 1.0, false);
//		t1.addInterval(1.0d, 2.0, true);
//		t1.addInterval(2.0d, 3.0, false);
		
		t2.addInterval(0.0d, 0.9, false);
		t2.addInterval(1.0d, 2.0, true);
		t2.addInterval(2.1d, 3.0, false);
		
		System.out.println(t1);
		System.out.println(t2);
		
		
		
	}
}
