package info.pinlab.snd.trs;

/**
 * 
 * Simple interval representation with final fields, no methods.
 * 
 * @author Gabor PINTER
 *
 * @param <T>
 */
public class Interval<T> {
	public final Double startT;
	public final Double endT;
	public final T label;

	public Interval(Double start, Double end, T label){
		this.startT = start;
		this.endT = end;
		this.label = label;
	}
	
	@Override
	public String toString(){
		return "|"+startT + ":" + endT + " " + label;
	}
}
