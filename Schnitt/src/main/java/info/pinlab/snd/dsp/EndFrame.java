package info.pinlab.snd.dsp;

public class EndFrame implements Frame {
	@Override
	public long getStartSampleIx() {
		return -1;
	}

}
