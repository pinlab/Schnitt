package info.pinlab.snd.fe;

public interface AudioFrameConsumer {
	public void consume(int[] samples);
	public void end();
}
