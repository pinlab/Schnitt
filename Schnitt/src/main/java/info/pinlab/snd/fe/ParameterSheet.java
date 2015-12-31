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
public class ParameterSheet{

	private final Map<String, FEParam<?>> paramMap;
	
	private ParameterSheet(Map<String, FEParam<?>> params){
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
	
	

	public static class ParameterSheetBuilder{
		public static Logger LOG = LoggerFactory.getLogger(ParameterSheetBuilder.class); 

		private final Map<String, FEParam<?>> rawParams;
		
		
		public ParameterSheetBuilder(){
			rawParams = new HashMap<String, FEParam<?>>();
			Object obj = FEParam.HZ;
			addParametersFromClass(FEParam.class);
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
//			try{
				for(Field field : clazz.getFields()){
					if(FEParam.class.isAssignableFrom(field.getType())){
						try {
							FEParam<?> param = (FEParam<?>) field.get(null);
							rawParams.put(param.getKey(), param);
							System.out.println(param.getKey() + " = " + param.get());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
//					for(Annotation anno : field.getAnnotations()){
//						if(anno instanceof ParamInt){
//							String paramLabel = field.getName();
//							String paramId    = clazz.getName() + "#" + paramLabel;
//							System.out.println(paramId);
////							int defVal = field.getInt(null);
////							rawParams.put(paramLabel, defVal);
////							System.out.println(paramLabel + " = " + defVal);
//						}
//
//					}
				}
//			} catch (IllegalAccessException ignore) {		}
			return this;
		}

		
		
		public ParameterSheetBuilder set(FEParam<?> param){
			FEParam<?> old = rawParams.get(param.getKey());
			if(!old.getClazz().equals(param.getClazz())){
				Object val = param.get();
				if(val!=null && val.equals(old.get())){ //-- if new value...
					LOG.warn("Param from '" + old.getClazz() + "." + old.getKey()+"'"
						+" overwritten with '" + param.getClazz() + "." + param.getKey()+"'");
				}else{
				}
			}
			if(param.get()==null && old.get()!=null){
				LOG.warn("Nullifying '" + old.getClazz() + "." + old.getKey()+"'");
			}
			rawParams.put(param.getKey(), param);
			return this;
		}
		
		public ParameterSheetBuilder set(FEParamBool param, boolean b){
			return set(new FEParamBool(param.getKey(), b, param.getClazz()));
		}
		public ParameterSheetBuilder set(FEParamInt param, int val){
			return set(new FEParamInt(param.getKey(), val, param.getClazz()));
		}
		public ParameterSheetBuilder set(FEParamDouble param, double val){
			return set(new FEParamDouble(param.getKey(), val, param.getClazz()));
		}
		public <T> ParameterSheetBuilder setParameter(FEParamObj<T> param, T value){
			return set(new FEParamObj<T>(param.getKey(), value, param.getClazz()));
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
		
		

		public ParameterSheetBuilder setAudioFormat(AudioFormat af){
			setHz((int)af.getSampleRate());
			setBytePerSample(af.getSampleSizeInBits()/8);
			isBigEndian(af.isBigEndian());
			return this;
		}
		public ParameterSheetBuilder setHz(int hz){
			return set(FEParam.HZ, hz);
		}
		public ParameterSheetBuilder setBytePerSample(int bytePerSample) {
			return set(FEParam.BYTE_PER_SAMPE, bytePerSample);
		}
		public ParameterSheetBuilder isBigEndian(boolean b){
			return set(FEParam.IS_BIG_ENDIAN, b);
		}
		public ParameterSheetBuilder setFrameLenInMs(int frameLenInMs){
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
		public ParameterSheetBuilder setFrameShiftInMs(int frameShiftInMs) {
			return set(FEParam.FRAME_LEN_MS, frameShiftInMs);
		}
		public ParameterSheetBuilder setWinType(WindowType winType){
			return setParameter(FEParam.WINDOW_TYPE, winType);
		}
		public ParameterSheet build(){
			return new ParameterSheet(rawParams);
		}
	}
}





