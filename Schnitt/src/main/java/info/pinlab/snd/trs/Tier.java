package info.pinlab.snd.trs;


/**
 * 
 * A tier holding time labelled information. <br>
 * Contracts: <ul>
 * <li> every tier starts at 0.0d time
 * <li> every time has t>=0.0d second duration
 * <li> every tier can have a name
 * </ul>
 * 
 * {@link AbstractIntervalTier } implements these characteristics.
 * 
 * 
 * 
 * @author kinoko
 *
 */
public interface Tier{
	enum Type{
		HYPO,
		TARG,
		VAD_EVAL,
		NOT_SET
	}
	
	public int size();
	public String getName();
	public void setName(String name);
	public double getDuration();
	
	public Type getTierType(); //-- set by init
//	public void setTierType(Type type);
	
}

