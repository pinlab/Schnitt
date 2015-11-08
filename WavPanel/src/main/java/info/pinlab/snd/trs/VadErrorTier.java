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
	Tier target = null;
	Tier hypo = null;
	
	public void setTargetTier(Tier tier){
		this.target = tier;
	}
	public void setHypoTier(Tier tier){
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

	
	public static void main(String[] args) {
	}
	
}
