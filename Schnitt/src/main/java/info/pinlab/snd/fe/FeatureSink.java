package info.pinlab.snd.fe;

/**
 * Consumer of frames, coming out from acoustic frontend pipeline.
 * 
 * It can be a file writer.
 * 
 * @author Gabor Pinter
 *
 */
public interface FeatureSink {
	
	/**
	 *  Should return instantly, not to fill up the buffer.
	 *  
	 * @param feature
	 */
	public void add(DoubleFrame feature);
	public void end();

}
