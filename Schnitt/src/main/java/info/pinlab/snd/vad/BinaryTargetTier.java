package info.pinlab.snd.vad;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;

public class BinaryTargetTier  extends BinaryTier{

	public BinaryTargetTier(BinaryTier tier){
		for(Interval<Boolean>  inter : tier){
			this.addInterval(inter);
		}
	}
	
	
	public BinaryTargetTier(){
		isEditable(true);
	}
}

