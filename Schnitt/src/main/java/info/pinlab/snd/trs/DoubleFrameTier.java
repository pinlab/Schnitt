package info.pinlab.snd.trs;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameTier{
	public static Logger LOG = LoggerFactory.getLogger(DoubleFrameTier.class);

	private final double hz ;
	private final int frameSz;
	private final double frameLenInMs;
	final TreeMap<Double, DoubleFrame> frames;

	/**
	 * 
	 * @param hz  sampling rate
	 * @param frameSize the size of frames in number of samples
	 */
	public DoubleFrameTier(int hz, int frameSize) {
		this.hz = (double)hz;
		this.frameSz = frameSize;
		frameLenInMs = (frameSize / (double)hz)*1000;
		frames = new TreeMap<Double, DoubleFrame>();
		frames.put(0.0d, null);
	}

	
	public void add(DoubleFrame frame){
		Double start = frame.getStartSampleIx()/hz;
		synchronized(this){
			frames.put(start, frame);
		}
	}
	
	
	public double  getFrameLenInMs(){
		return frameLenInMs;
	}
	
	synchronized public List<Double> getTimeLabels(){
		return new ArrayList<Double>(frames.keySet());
	}
	 
	/**
	 * Gets frame at timepoint
	 * 
	 * @param t
	 */
	public DoubleFrame getFrameAt(double t){
		DoubleFrame frame = frames.get(t);
		if(frame!=null){
			return frame;
		}else{
			Double key = frames.floorKey(t);
			if(key!=null && t <= (key+frameLenInMs)){ //- t is within key---key+frameLen
				return  frames.get(key);
			}else{
				return null;
			}
		}
	}
	
	 /**
	  * @return returns the number of DoubleFrames
	  */
	 synchronized public int size(){
		 return frames.size();
	 }

}
