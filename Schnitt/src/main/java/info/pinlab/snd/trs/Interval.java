package info.pinlab.snd.trs;

/**
 * 
 * Simple interval representation with final fields, no methods.
 * 
 * @author Gabor PINTER
 *
 * @param <T> Type of the lables. E.g., boolean or String.
 */
public class Interval<T> {
	public final Double startT;
	public final Double endT;
	public final T label;
	public Double conf;
	
	
	public Interval(Double start, Double end, T label){
		this.startT = start;
		this.endT = end;
		this.label = label;
	}

	
	public void setConf(double conf){
		this.conf = conf;
	}
	
	@Override
	public String toString(){
		return "|"+startT + ":" + endT + " " + label  + (this.conf!=null ? conf : "");
	}
}
