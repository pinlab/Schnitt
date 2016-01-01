package info.pinlab.ml.core;

import java.util.ArrayList;
import java.util.List;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameAccumulator implements Accumulator<DoubleFrame> {
	private String label;
	private final List<DoubleFrame> frames;
	
	
	public DoubleFrameAccumulator(String label){
		this.label = label;
		this.frames = new ArrayList<DoubleFrame>();
	}
	
	
	@Override
	public void add(DoubleFrame data) {
		frames.add(data);
	}

	@Override
	public int size() {
		return frames.size();
	}

	public String getLabel(){
		return label;
	}
	
}
