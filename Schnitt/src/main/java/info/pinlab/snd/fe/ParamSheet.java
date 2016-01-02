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
		return ((FEParamInt)key).get();
	}

	public double get(FEParamDouble param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamDouble)key).get();
	}



	public boolean get(FEParamBool param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamBool)key).get();
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
			return this;
		}


		public ParamSheetBuilder set(FEParam<?> param){
			LOG.info("Setting " + param);
			FEParam<?> old = rawParams.get(param.getKey());
			if(old!=null){
				if(!old.getParentClazz().equals(param.getParentClazz())){
					Object val = param.get();
					if(val!=null && !val.equals(old.get())){ //-- if new value...
						LOG.warn("Overwriting " + old);
					}else{
						LOG.trace("Shadowing " + old);
					}
				}
			}
			if(param.get()==null && old.get()!=null){
				LOG.warn("Nullifying '" + old.getParentClazz() + "." + old.getKey()+"'");
			}
			rawParams.put(param.getKey(), param);
			return this;
		}

		public ParamSheetBuilder set(FEParamBool param, boolean b){
			return set(new FEParamBool(param.getKey(), b, param.getParentClazz()));
		}
		public ParamSheetBuilder set(FEParamInt param, int val){
			return set(new FEParamInt(param.getKey(), val, param.getParentClazz()));
		}
		public ParamSheetBuilder set(FEParamDouble param, double val){
			return set(new FEParamDouble(param.getKey(), val, param.getParentClazz()));
		}
		public <T> ParamSheetBuilder setParameter(FEParamObj<T> param, T value){
			return set(new FEParamObj<T>(param.getKey(), value, param.getParentClazz()));
		}


		public Integer get(FEParamInt param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamInt)par).get();
		}
		public Double get(FEParamDouble param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamDouble)par).get();
		}
		public Boolean get(FEParamBool param){
			FEParam<?> par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamBool)par).get();
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

