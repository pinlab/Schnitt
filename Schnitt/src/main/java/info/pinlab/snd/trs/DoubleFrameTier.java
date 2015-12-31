package info.pinlab.snd.trs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameTier extends AbstractIntervalTier<DoubleFrame> {
	public static Logger LOG = LoggerFactory.getLogger(DoubleFrameTier.class);

	private final double hz ;
	private final int frameSz;
	
	public DoubleFrameTier(Type type, double hz, int frameSize) {
		super(Type.NOT_SET);
		this.hz = hz;
		this.frameSz = frameSize;
	}

	
	public void addInterval(DoubleFrame frame){
		double from = frame.getStartSampleIx()/hz;
		double to   = (frame.getStartSampleIx()+this.frameSz)/hz;
		addInterval(from, to, frame);
	}
	
	@Override
	public IntervalTier<DoubleFrame> addInterval(double from, double to, DoubleFrame frame){
		if(from==to){
			from = frame.getStartSampleIx()/hz;
			to   = (frame.getStartSampleIx()+this.frameSz)/hz;
		}
		points.put(from, frame);
		return this;
	}
	
	
	
	@Override
	public IntervalTier<DoubleFrame> addInterval(Interval<DoubleFrame> interval){
		return addInterval(interval.startT, interval.endT, interval.label);
	}

	
	
	@Override
	public DoubleFrame combineLabels(List<DoubleFrame> labels) {
		return null;
	}

	

}
