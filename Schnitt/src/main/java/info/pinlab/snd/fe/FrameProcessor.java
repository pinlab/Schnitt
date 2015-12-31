package info.pinlab.snd.fe;


/**
 * 
 * Acoustic FrontEnd pipelines are composed of FramProcessors. 
 * Implementation contracts
 * <ul>
 * 	<li> implementations must extend {@link AbstractFrameProcessor}
 * 	<li> static
 * </ul>
 * 
 * 
 * 
 * @author Gabor Pinter
 *
 */
public interface FrameProcessor{

	/**
	 * @return String key for data to process
	 */
	public String getPredecessorKey();
	/**
	 * @return key of this processor
	 */
	public String getKey();

	
	public void setPredecessorKey(String key);
	/**
	 * @return key of this processor
	 */
	public void setKey(String key);
	
	
	public void init();
	public void processWrapper(DoubleFrame frame);
	public double [] process(double [] arr);
	
}

