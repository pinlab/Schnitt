package info.pinlab.snd.dsp;


/**
 * Implementations must extend {@link AbstractFrameProcessor}.
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
	 * 
	 * @return key of this processor
	 */
	public String getKey();
	
	
	public void init();
	public void processWrapper(DoubleFrame frame);
	public double [] process(double [] arr);
	
}

