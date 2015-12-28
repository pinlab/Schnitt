package info.pinlab.snd.dsp;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import info.pinlab.snd.dsp.Windower.WindowType;

/**
 * Collection of acoustic related configuration
 * 
 * @author Gabor Pinter
 *
 */
public class ParameterSheet{
	//-- default values --//
//	public static int HZ = 16000;
//	public static int BYTE_PER_SAMPLE = 2;
//	public static boolean IS_BIG_ENDIAN = false;
//	public static int FRAME_LEN_MS = 20;
//	public static int FRAME_SHIFT_MS = 10;
//	
//	public static int FFT_N = 128;
//	public static int MFCC_CH = 26;
//	public static WindowType WIN_TYPE = Windower.WindowType.HANNING;
//	
//	private final int hz ;
//	private final boolean isBigEndian;
//	private final int fftN ;
//	public final int mfccCh ;
//	private final int frameLenInMs;
//	private final int frameShiftInMs;
//	public final WindowType winType;
//	private final int frameLenInByte; 
//	private final int bytePerSample;
//	private final int frameLenInSample;
//	
	interface ProcessorParameter{
		public String getUniqName();
		public String getLabel();
		
		public boolean getBoolean();
		public int getInt();
		public double getDouble();
		public Object get();
	}
	
	enum BaseParams implements ProcessorParameter{
		HZ(16000),
		BYTE_PER_SAMPE(2),
		IS_BIG_ENDIAN(false),
		FRAME_LEN_MS(20),
		FRAME_LEN_SAMPLE(20*16),
		FRAME_LEN_BYTE(20*16*2),
		FRAME_SHIFT_MS(20 / 2),
		WIN_TYPE(WindowType.HANNING)
		;
		
		final String id;
		final String label;
		final Object defVal;
		BaseParams(Object defVal){
			this.id = FrameProducer.class.getName()+"." +this.toString().toUpperCase();
			this.label = id.toUpperCase();
			this.defVal = defVal;
		}

		@Override
		public String getUniqName(){return id;			}
		@Override
		public String getLabel() {	return label;		}
		@Override
		public boolean getBoolean(){return (boolean)defVal;	}
		@Override
		public int getInt() {		return (int)defVal;	}
		@Override
		public double getDouble(){	return (int) defVal;}
		@Override
		public Object get(){		return defVal;		}
	}
	
	private final Map<ProcessorParameter, Object> paramValues;
	public static ProcessorParameter[] getProcessorParams(){
		return BaseParams.values();
	}
	

	
	
	private ParameterSheet(Map<ProcessorParameter, Object> paramValues){
//			int hz, int bytePerSample, boolean isBigEndian,
//			 int frameInMs, int frameShiftInMs, WindowType win,
//			 int fftN, int mfccCh){
//		this.hz = hz;
//		this.isBigEndian = isBigEndian;
//		this.bytePerSample = bytePerSample;
//		this.fftN = fftN;
//		this.mfccCh = mfccCh;
//		this.winType = WIN_TYPE;
//		this.frameLenInMs = frameInMs;
//		this.frameLenInByte = (frameLenInMs*hz*bytePerSample)/1000;   /* */; //-- 10ms x 16kHz x depth (16bit -> 2byte)
//		this.frameLenInSample = this.frameLenInByte / this.bytePerSample ; 
//		this.frameShiftInMs = frameShiftInMs;
		
		this.paramValues = new HashMap<ProcessorParameter, Object>(paramValues);
	}

	
	public int getInt(ProcessorParameter par){
		return (int)paramValues.get(par);
	}
	
	
	
	
	public static class ParameterSheetBuilder{
//		private int hz = AcousticParameterSheet.HZ ;
//		private boolean isBigEndian = AcousticParameterSheet.IS_BIG_ENDIAN;
//		private int fftN = AcousticParameterSheet.FFT_N;
//		private int mfccCh = AcousticParameterSheet.MFCC_CH;
//		private int frameLenInMs = AcousticParameterSheet.FRAME_LEN_MS;
//		private int frameShiftInMs = AcousticParameterSheet.FRAME_SHIFT_MS;
//		private WindowType winType = AcousticParameterSheet.WIN_TYPE;
//		private int bytePerSample = BYTE_PER_SAMPLE;
		
		private final Map<ProcessorParameter, Object> params = new HashMap<ProcessorParameter, Object>();

		
		public ParameterSheetBuilder(){
			//-- add default value
			for(ProcessorParameter param : ParameterSheet.getProcessorParams()){
				System.out.println(param.getUniqName());
				params.put(param, param.get());
			}
			for(ProcessorParameter param : MelFilter.getProcessorParams()){
				System.out.println(param.getUniqName());
				params.put(param, param.get());
			}
		}
		
		

		public void addParameter(ProcessorParameter param){
			params.put(param, param.get());
		}

		public void setParameter(ProcessorParameter param, Object val){
			params.put(param, val);
		}
		
		
		
		public ParameterSheetBuilder setAudioFormat(AudioFormat af){
			setHz((int)af.getSampleRate());
			setBytePerSample(af.getSampleSizeInBits()/8);
			isBigEndian(af.isBigEndian());
			return this;
		}
		public ParameterSheetBuilder setHz(int hz){
			params.put(BaseParams.HZ, hz);
			return this;
		}
		public ParameterSheetBuilder setBytePerSample(int bytePerSample) {
			params.put(BaseParams.BYTE_PER_SAMPE, bytePerSample);
			return this;
		}
		public ParameterSheetBuilder isBigEndian(boolean b){
			params.put(BaseParams.IS_BIG_ENDIAN, b);
			return this;
		}
		public ParameterSheetBuilder setFrameLenInMs(int frameLenInMs) {
			params.put(BaseParams.FRAME_LEN_MS, frameLenInMs);
			params.put(BaseParams.FRAME_SHIFT_MS, frameLenInMs/2);
			int frameLenInSample = frameLenInMs*((int)params.get(BaseParams.HZ))/1000;
			params.put(BaseParams.FRAME_LEN_SAMPLE, frameLenInSample);
			params.put(BaseParams.FRAME_LEN_BYTE,   frameLenInSample*(int)params.get(BaseParams.BYTE_PER_SAMPE));
			return this;
		}
		public ParameterSheetBuilder setFrameShiftInMs(int frameShiftInMs) {
			params.put(BaseParams.FRAME_SHIFT_MS, frameShiftInMs);
			return this;
		}
//		public AcousticParameterSheetBuilder setFftN(int fftN) {
//			params.put(BaseParams.HZ, hz);
//			this.fftN = fftN;
//			return this;
//		}
//		public AcousticParameterSheetBuilder setMfccCh(int mfccCh) {
//			this.mfccCh = mfccCh;
//			return this;
//		}
		public ParameterSheetBuilder setWinType(WindowType winType) {
			params.put(BaseParams.WIN_TYPE, winType);
			return this;
		}
		
		
		public ParameterSheet build(){
			return new ParameterSheet(params);
//			return new AcousticParameterSheet(hz, bytePerSample, isBigEndian,
//					frameLenInMs, frameShiftInMs, winType, fftN, mfccCh);
		}
	}
}





