package info.pinlab.snd.vad;

import info.pinlab.snd.trs.BinaryTier;

/**
 * 
 * A binary tier bound with confidence/probablity values 
 * 
 * @author Gabor Pinter
 *
 */
public class BinaryHypoTier extends BinaryTier{

	public BinaryHypoTier(){
		super(Type.HYPO);
		isEditable(true);
	}
	
	
//	public IntervalTier<Boolean> addInterval(Interval<Boolean> interval, double conf){
//		return addInterval(interval.startT, interval.endT, (boolean)(interval.label==null? false: interval.label));
//	}
//
//	
//	public BinaryHypoTier addInterval(double from, double to, Boolean b, double conf){
//		addInterval(from,to,b);
//		return this;
//	}

	
}

