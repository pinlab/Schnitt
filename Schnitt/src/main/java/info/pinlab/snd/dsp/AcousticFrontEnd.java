package info.pinlab.snd.dsp;

public interface AcousticFrontEnd {
	public void setSamples(int[] samples);

	public double[] getFft();

	public double[] getMfcc();

	public boolean next();
}
