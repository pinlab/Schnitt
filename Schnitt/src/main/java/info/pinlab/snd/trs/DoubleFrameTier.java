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
//	private final int frameSz;
	private final double frameLenInSec;
	final TreeMap<Double, DoubleFrame> frames;
	
	
	/**
	 * 
	 * @param hz  sampling rate
	 * @param frameSizeMs size of frame in ms
	 */
	public DoubleFrameTier(int hz, int frameSizeMs) {
		this.hz = (double)hz;
//		this.frameSz = (int)(frameSizeMs*hz/1000.0d);
		this.frameLenInSec = frameSizeMs / 1000.0d;
		System.out.println("FRAME LEN IN SEC " + frameLenInSec);
		
		frames = new TreeMap<Double, DoubleFrame>();
		frames.put(0.0d, null);
	}

	
	public void add(DoubleFrame frame){
		Double start = frame.getStartSampleIx()/hz;
		synchronized(this){
			frames.put(start, frame);
		}
	}
	
	
	public Double getStartT(){
		if(frames.size()==0){
			return null;
		}
		return frames.firstKey();
	}

	public Double getEndT(){
		if(frames.size()==0){
			return null;
		}
		return frames.lastKey() + frameLenInSec;
	}
	
	public double  getFrameLenInSec(){
		return frameLenInSec;
	}
//	public double  getFrameLenInMs(){
//		return frameLenInMs;
//	}
	
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
			if(key!=null && t <= (key+frameLenInSec)){ //- t is within key---key+frameLen
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
