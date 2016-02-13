package info.pinlab.ml.core;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameAccumulator<T> extends Accumulator<T, DoubleFrame> {
	
	
	public DoubleFrameAccumulator(T label, int dim){
		super(label, dim);
	}
	
	
}
