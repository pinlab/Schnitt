package info.pinlab.snd.trs;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameTier{
	public static Logger LOG = LoggerFactory.getLogger(DoubleFrameTier.class);

	private final double hz ;
	private final int frameSz;
	private final double frameLen;
	final TreeMap<Double, DoubleFrame> frames;

	
	public DoubleFrameTier(int hz, int frameSize) {
		this.hz = (double)hz;
		this.frameSz = frameSize;
		frameLen = frameSize / hz;
		frames = new TreeMap<Double, DoubleFrame>();
		frames.put(0.0d, null);
	}

	
	public void add(DoubleFrame frame){
		Double start = frame.getStartSampleIx()/hz;
		synchronized(this){
			frames.put(start, frame);
		}
	}
	
	
	synchronized public Set<Double> getTimeLabels(){
		return new HashSet<Double>(frames.keySet());
	}
	 
	 /**
	  * @return returns the number of DoubleFrames
	  */
	 synchronized public int size(){
		 return frames.size();
	 }

}
