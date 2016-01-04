package info.pinlab.snd.fe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.fe.Windower.WindowType;

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
public class ParamSheet{

	private final Map<String, FEParam<?>> paramMap;

	private ParamSheet(Map<String, FEParam<?>> params){
		paramMap = new HashMap<String, FEParam<?>>(params);
	}

	public boolean containsKey(FEParamInt param){
		return paramMap.containsKey(param.getKey());
	}

	public Integer get(FEParamInt param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamInt)key).getValue();
	}
	public double get(FEParamDouble param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamDouble)key).getValue();
	}
	public boolean get(FEParamBool param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamBool)key).getValue();
	}
	public String get(FEParamString param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamString)key).getValue();
	}



	public static class ParamSheetBuilder{
		public static Logger LOG = LoggerFactory.getLogger(ParamSheetBuilder.class); 

		private final Map<String, FEParam<?>> rawParams;


		public ParamSheetBuilder(){
			rawParams = new HashMap<String, FEParam<?>>();
			addParametersFromClass(FEParam.class);
		}



		/**
		 * Adds parameters from FrameProcessor. Parameters are annotated by {@link ProcessorParam} 
		 * 
		 * @param fqcn
		 * @return 
		 */
		public ParamSheetBuilder addParametersFromClass(String fqcn){
			try{
				Class<?> clazz = Class.forName(fqcn);
				return addParametersFromClass(clazz);
			}catch(ClassNotFoundException e){
				LOG.warn("No such class as '" + fqcn + "'");
			}
			return this;
		}

		public ParamSheetBuilder addParametersFromClass(Class<?> clazz){
			LOG.info("Adding parameters from '" + clazz.getName() +"'");
			
			
			for(Field field : clazz.getFields()){
				if(FEParam.class.isAssignableFrom(field.getType())){
					try {
						FEParam<?> param = (FEParam<?>) field.get(null);
						set(param);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
//			//-- if frame processor, add automatically
//			if(FrameProcessor.class.isAssignableFrom(clazz)){
//				String processors = get(FEParamString.FRAME_PROCESSORS);
//				if(processors==null){
//					processors=clazz.getName();
//				}else{
//					processors+=":"+clazz.getName();
//				}
//				set(FEParam.FRAME_PROCESSORS, processors);
//			}
			return this;
		}


		public ParamSheetBuilder set(FEParam<?> param){
			LOG.info("Setting " + param);
			FEParam<?> old = rawParams.get(param.getKey());
			if(old!=null){
				if(!old.getParentClazz().equals(param.getParentClazz())){
					Object val = param.getValue();
					if(val!=null && !val.equals(old.getValue())){ //-- if new value...
						LOG.warn("Overwriting " + old);
					}else{
						LOG.trace("Shadowing " + old);
					}
				}
				if(param.getValue()==null && old.getValue()!=null){
					LOG.warn("Nullifying '" + old.getParentClazz() + "." + old.getKey()+"'");
				}
			}
			rawParams.put(param.getKey(), param);
			
			return this;
		}

		public ParamSheetBuilder set(FEParamBool param, boolean b){
			return set(new FEParamBool(param.getKey(), b, param.getParentClazz()));
		}
		public ParamSheetBuilder set(FEParamInt param, int val){
			set(new FEParamInt(param.getKey(), val, param.getParentClazz()));
			//-- adjusting for special cases
			if(FEParamInt.FRAME_LEN_MS.getKey().equals(param.getKey())){
				int hz = get(FEParam.HZ);
				int sampleLen = (int)(hz*val/1000.0d);
				rawParams.put(FEParam.FRAME_LEN_SAMPLE.getKey(), 
						new FEParamInt(FEParam.FRAME_LEN_SAMPLE.getKey(), sampleLen, FEParam.class));
				int byteLen = sampleLen * get(FEParamInt.BYTE_PER_SAMPE);
				rawParams.put(FEParam.FRAME_LEN_BYTE.getKey(), 
						new FEParamInt(FEParam.FRAME_LEN_BYTE.getKey(), byteLen, FEParam.class));
			}else
			if(FEParamInt.HZ.getKey().equals(param.getKey())){
				int sampleLen  = get(FEParam.FRAME_LEN_SAMPLE);
				int byteLen = sampleLen * get(FEParamInt.BYTE_PER_SAMPE);
				rawParams.put(FEParam.FRAME_LEN_BYTE.getKey(), 
						new FEParamInt(FEParam.FRAME_LEN_BYTE.getKey(), byteLen, FEParam.class));
			}else
			if(FEParamInt.BYTE_PER_SAMPE.getKey().equals(param.getKey())){
				int sampleLen  = get(FEParam.FRAME_LEN_SAMPLE);
				int byteLen = sampleLen * get(FEParamInt.BYTE_PER_SAMPE);
				rawParams.put(FEParam.FRAME_LEN_BYTE.getKey(), 
						new FEParamInt(FEParam.FRAME_LEN_BYTE.getKey(), byteLen, FEParam.class));
			}
			return this; 
		}
		public ParamSheetBuilder set(FEParamDouble param, double val){
			return set(new FEParamDouble(param.getKey(), val, param.getParentClazz()));
		}
		public ParamSheetBuilder set(FEParamString param, String val){
			if(FEParam.FRAME_PROCESSORS.equals(param)){
				for(String processor : val.split(":")){
					if(processor!=null && !processor.isEmpty())
						this.addParametersFromClass(processor);
				}
			}
			return set(new FEParamString(param.getKey(), val, param.getParentClazz()));
		}
		public <T> ParamSheetBuilder setParameter(FEParamObj<T> param, T value){
			return set(new FEParamObj<T>(param.getKey(), value, param.getParentClazz()));
		}


		public Integer get(FEParamInt param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamInt)par).getValue();
		}
		public Double get(FEParamDouble param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamDouble)par).getValue();
		}
		public Boolean get(FEParamBool param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamBool)par).getValue();
		}

		public String get(FEParamString param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamString)par).getValue();
		}
		


		public ParamSheetBuilder setAudioFormat(AudioFormat af){
			setHz((int)af.getSampleRate());
			setBytePerSample(af.getSampleSizeInBits()/8);
			isBigEndian(af.isBigEndian());
			return this;
		}
		public ParamSheetBuilder setHz(int hz){
			return set(FEParam.HZ, hz);
		}
		public ParamSheetBuilder setBytePerSample(int bytePerSample) {
			return set(FEParam.BYTE_PER_SAMPE, bytePerSample);
		}
		public ParamSheetBuilder isBigEndian(boolean b){
			return set(FEParam.IS_BIG_ENDIAN, b);
		}
		public ParamSheetBuilder setFrameLenInMs(int frameLenInMs){
			set(FEParam.FRAME_LEN_MS, frameLenInMs);
			set(FEParam.FRAME_SHIFT_MS, frameLenInMs/2);

			Integer hz = get(FEParam.HZ);
			if(hz!=null){
				int sampleLen = (frameLenInMs*hz)/1000;
				set(FEParam.FRAME_LEN_SAMPLE, sampleLen);
				Integer byteLen = get(FEParam.BYTE_PER_SAMPE);
				if(byteLen!=null){
					set(FEParam.FRAME_LEN_BYTE, sampleLen*byteLen);
				}
			}
			return this;
		}
		public ParamSheetBuilder setFrameShiftInMs(int frameShiftInMs) {
			return set(FEParam.FRAME_LEN_MS, frameShiftInMs);
		}
		public ParamSheetBuilder setWinType(WindowType winType){
			return setParameter(FEParam.WINDOW_TYPE, winType);
		}
		public ParamSheet build(){
			return new ParamSheet(rawParams);
		}
	}
}

