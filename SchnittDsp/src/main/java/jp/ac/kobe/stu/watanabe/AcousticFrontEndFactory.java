package jp.ac.kobe.stu.watanabe;

public interface AcousticFrontEndFactory {

	public AcousticFrontEndFactory setFftN(int fft);

	public AcousticFrontEndFactory setMfccCh(int mfcc);

	public AcousticFrontEndFactory setWindowType(String wind);

	public AcousticFrontEndFactory setStepLength(int step);

	public AcousticFrontEndFactory setWindowLength(int winLen);

	public AcousticFrontEndFactory setHz(int hz);

	public AcousticFrontEnd build();

}
