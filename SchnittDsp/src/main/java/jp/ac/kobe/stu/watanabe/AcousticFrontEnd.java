package jp.ac.kobe.stu.watanabe;

public interface AcousticFrontEnd {
	public void setSamples(int[] samples);

	public double[] getFft();

	public double[] getMfcc();

	public boolean next();
}
