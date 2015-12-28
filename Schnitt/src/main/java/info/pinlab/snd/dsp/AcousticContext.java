package info.pinlab.snd.dsp;

import javax.sound.sampled.AudioFormat;

import info.pinlab.snd.dsp.Windower.WindowType;

/**
 * Collection of acoustic related configuration
 * 
 * @author Gabor Pinter
 *
 */
public class AcousticContext{
	//-- default values --//
	public static int HZ = 16000;
	public static int BYTE_PER_SAMPLE = 2;
	public static boolean IS_BIG_ENDIAN = false;
	public static int FFT_N = 128;
	public static int MFCC_CH = 26;
	public static int FRAME_LEN_MS = 20;
	public static int FRAME_SHIFT_MS = 10;
	public static WindowType WIN_TYPE = Windower.WindowType.HANNING;
	
	public final int hz ;
	public final boolean isBigEndian;
	public final int fftN ;
	public final int mfccCh ;
	public final int frameLenInMs;
	public final int frameShiftInMs;
	public final WindowType winType;
	public final int frameLenInByte; 
	public final int bytePerSample;
	public final int frameLenInSample;
	
	private AcousticContext(int hz, int bytePerSample, boolean isBigEndian,
			 int frameInMs, int frameShiftInMs, WindowType win,
			 int fftN, int mfccCh){
		this.hz = hz;
		this.isBigEndian = isBigEndian;
		this.bytePerSample = bytePerSample;
		this.fftN = fftN;
		this.mfccCh = mfccCh;
		this.winType = WIN_TYPE;
		this.frameLenInMs = frameInMs;
		this.frameLenInByte = (frameLenInMs*hz*bytePerSample)/1000;   /* */; //-- 10ms x 16kHz x depth (16bit -> 2byte)
		this.frameLenInSample = this.frameLenInByte / this.bytePerSample ; 
		this.frameShiftInMs = frameShiftInMs;
	}
	
	public static class AcousticContextBuilder{
		private int hz = AcousticContext.HZ ;
		private boolean isBigEndian = AcousticContext.IS_BIG_ENDIAN;
		private int fftN = AcousticContext.FFT_N;
		private int mfccCh = AcousticContext.MFCC_CH;
		private int frameLenInMs = AcousticContext.FRAME_LEN_MS;
		private int frameShiftInMs = AcousticContext.FRAME_SHIFT_MS;
		private WindowType winType = AcousticContext.WIN_TYPE;
		private int bytePerSample = BYTE_PER_SAMPLE;

		public AcousticContextBuilder setAudioFormat(AudioFormat af) {
			this.hz = (int)af.getSampleRate();
			this.bytePerSample = af.getSampleSizeInBits()/8;
			isBigEndian = af.isBigEndian();
			return this;
		}
		
		
		public AcousticContextBuilder setHz(int hz) {
			this.hz = hz;
			return this;
		}
		public AcousticContextBuilder setFftN(int fftN) {
			this.fftN = fftN;
			return this;
		}
		public AcousticContextBuilder setMfccCh(int mfccCh) {
			this.mfccCh = mfccCh;
			return this;
		}
		public AcousticContextBuilder setFrameLenInMs(int frameLenInMs) {
			this.frameLenInMs = frameLenInMs;
			return this;
		}
		public AcousticContextBuilder setFrameShiftInMs(int frameShiftInMs) {
			this.frameShiftInMs = frameShiftInMs;
			return this;
		}
		public AcousticContextBuilder setWinType(WindowType winType) {
			this.winType = winType;
			return this;
		}
		public AcousticContextBuilder setBytePerSample(int bytePerSample) {
			this.bytePerSample = bytePerSample;
			return this;
		}
		
		
		public AcousticContext build(){
			return new AcousticContext(hz, bytePerSample, isBigEndian,
					frameLenInMs, frameShiftInMs, winType, fftN, mfccCh);
		}
	}
}





