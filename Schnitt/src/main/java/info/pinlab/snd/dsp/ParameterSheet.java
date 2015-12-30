package info.pinlab.snd.dsp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.dsp.Windower.WindowType;

/**
 * Collection of acoustic related configuration
 * 
 * 
- key: 
  - extendable 
  - not referencing by string...

- abstract: initi fields from context

- order
 1) name of args < before init
 2) default value < before init
 
 - clash
 diff proc. classes should be able to use same param (e.g., HZ)

 * 
 * @author Gabor Pinter
 *
 */
public class ParameterSheet{

	private final Map<ProcessorParameter, Object> paramValues;
	private final Map<String, Object> rawParamValues;

	
	
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
		/**
		 * Byte / sample : 16bit=2byte
		 */
		BYTE_PER_SAMPE(2),
		/**
		 * Endianness of PCM encoding.
		 */
		IS_BIG_ENDIAN(false),
		/**
		 * Length of a frame in ms
		 */
		FRAME_LEN_MS(20),
		/**
		 * Length of a frame in number of samples
		 */
		FRAME_LEN_SAMPLE(20*16),
		/**
		 * Length of a frame in number of bytes
		 */
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

	public static ProcessorParameter[] getProcessorParams(){
		return BaseParams.values();
	}




	private ParameterSheet(Map<ProcessorParameter, Object> params, Map<String, Object> raws){
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

		paramValues = new HashMap<ProcessorParameter, Object>(params);
		rawParamValues = new HashMap<String, Object>(raws);
	}


	public int getInteger(String param){
		return (int)rawParamValues.get(param);
	}
	
	
	/**
	 * 
	 * @param par
	 * @return null if not key, or key has no value
	 */
	public Integer getInt(ProcessorParameter par){
		return (int)paramValues.get(par);
	}


	
	
	

	public static class ParameterSheetBuilder{
		public static Logger LOG = LoggerFactory.getLogger(ParameterSheetBuilder.class); 

		@ParamInt
		public static final int HZ = 16000;
		public static final String PARAM_HZ = "HZ";
		@ParamInt
		public static final int BYTE_PER_SAMPE = 2;
		public static final String PARAM_BYTE_PER_SAMPE = "BYTE_PER_SAMPE";
		@ParamInt
		public static final int FRAME_LEN_MS = 20;
		public static final String PARAM_FRAME_LEN_MS = "FRAME_LEN_MS";
		@ParamInt
		public static final int FRAME_LEN_SAMPLE = 20*16;
		public static final String PARAM_FRAME_LEN_SAMPLE = "FRAME_LEN_SAMPLE";
		@ParamInt
		public static final int FRAME_LEN_BYTE = 20*16*2;
		public static final String PARAM_FRAME_LEN_BYTE = "FRAME_LEN_BYTE";
		@ParamInt
		public static final int FRAME_SHIFT_MS = (20/2);
		public static final String PARAM_FRAME_SHIFT_MS = "FRAME_SHIFT_MS";
		
		public static final boolean IS_BIG_ENDIAN = false;
//		WindowType = WindowType.HANNING;
		
		private final Map<ProcessorParameter, Object> params = new HashMap<ProcessorParameter, Object>();
		private final Map<String, Object> rawParams;
		
		public ParameterSheetBuilder(){
			rawParams = new HashMap<String, Object>();
			addParametersFromClass(this.getClass());
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


		/**
		 * Adds parameters from FrameProcessor. Parameters are annotated by {@link ProcessorParam} 
		 * 
		 * @param fqcn
		 * @return 
		 */
		public ParameterSheetBuilder addParametersFromClass(String fqcn){
			try{
				Class<?> clazz = Class.forName(fqcn);
				return addParametersFromClass(clazz);
			}catch(ClassNotFoundException e){
				LOG.warn("No such class as '" + fqcn + "'");
			}
			return this;
		}
		
		public ParameterSheetBuilder addParametersFromClass(Class<?> clazz){
			try{
				for(Field field : clazz.getFields()){
					for(Annotation anno : field.getAnnotations()){
						if(anno instanceof ParamInt){
							String paramLabel = field.getName();
							String paramId    = clazz.getName() + "#" + paramLabel;
							System.out.println(paramId);
							int defVal = field.getInt(null);
							rawParams.put(paramLabel, defVal);
							System.out.println(paramLabel + " = " + defVal);
						}

					}
				}
			} catch (IllegalAccessException ignore) {		}
			return this;
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
//			params.put(BaseParams.HZ, hz);
			rawParams.put(PARAM_HZ, hz);
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
		public ParameterSheetBuilder setFrameLenInMs(int frameLenInMs){
			rawParams.put(PARAM_FRAME_LEN_MS,    frameLenInMs);
			rawParams.put(PARAM_FRAME_SHIFT_MS,  frameLenInMs/2);
			int sampleLen = frameLenInMs*((int)rawParams.get(PARAM_HZ))/1000;
			rawParams.put(PARAM_FRAME_LEN_SAMPLE,sampleLen);
			rawParams.put(PARAM_FRAME_LEN_BYTE,  
					(int)rawParams.get(PARAM_FRAME_LEN_SAMPLE)
					*(int)rawParams.get(PARAM_BYTE_PER_SAMPE)
					);

			
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
			return new ParameterSheet(params, rawParams);
			//			return new AcousticParameterSheet(hz, bytePerSample, isBigEndian,
			//					frameLenInMs, frameShiftInMs, winType, fftN, mfccCh);
		}
	}
}





