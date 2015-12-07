package info.pinlab.snd.vad;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.IntervalTier;

/**
 * 
 * A binary tier bound with confidence/probablity values 
 * 
 * @author Gabor Pinter
 *
 */
public class VadHypoTier extends BinaryTier{

	public VadHypoTier(){
		
	}
	
	public IntervalTier<Boolean> addInterval(Interval<Boolean> interval, double conf){
		return addInterval(interval.startT, interval.endT, (boolean)(interval.label==null? false: interval.label));
	}

	
	public VadHypoTier addInterval(double from, double to, Boolean b, double conf){
		addInterval(from,to,b);
		return this;
	}

	
}

