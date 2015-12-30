package info.pinlab.snd.dsp;

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

	private final Map<String, FEParam> paramMap;

	private ParameterSheet(Map<String, FEParam > params){
		paramMap = new HashMap<String, FEParam>(params);
	}
	
	public boolean containsKey(FEParamInt param){
		return paramMap.containsKey(param.getKey());
	}
	
	public int get(FEParamInt param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamInt)key).getValue();
	}
	public boolean get(FEParamBool param){
		Object key = paramMap.get(param.getKey());
		if(key==null){
			throw new IllegalArgumentException("No such key as '" + param.getKey() +"'");
		}
		return ((FEParamBool)key).getValue();
	}
	
	

	public static class ParameterSheetBuilder{
		public static Logger LOG = LoggerFactory.getLogger(ParameterSheetBuilder.class); 

		private final Map<String, FEParam> rawParams;
		
		
		public ParameterSheetBuilder(){
			rawParams = new HashMap<String, FEParam>();
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
							FEParam param = (FEParam) field.get(null);
							rawParams.put(param.getKey(), param);
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

		
		private boolean overWriteWarning(FEParam p1, FEParam p2){
			if(!p1.getClazz().equals(p2.getClazz())){
				LOG.warn("Param from '" + p1.getClazz() + "." + p1.getKey()+"'"
						+" overwritten with '" + p2.getClazz() + "." + p1.getKey()+"'");
				return true;
			}
			return false;
		}

		public ParameterSheetBuilder setParameter(FEParamInt param, int val){
			FEParam oldParam = rawParams.get(param.getKey());
			FEParam newParam = new FEParamInt(param.getKey(), val, param.getClazz());
			overWriteWarning(oldParam, newParam);
			rawParams.put(newParam.getKey(), newParam);
			return this;
		}
		public ParameterSheetBuilder setParameter(FEParamBool param, boolean b){
			FEParam oldParam = rawParams.get(param.getKey());
			FEParam newParam = new FEParamBool(param.getKey(), b, param.getClazz());
			overWriteWarning(oldParam, newParam);
			rawParams.put(newParam.getKey(), newParam);
			return this;
		}

		
		public <T> ParameterSheetBuilder setParameter(FEParamObj<T> param, T value){
			FEParam oldParam = rawParams.get(param.getKey());
			FEParam newParam = new FEParamObj<T>(param.getKey(), value, param.getClazz());
			overWriteWarning(oldParam, newParam);
			rawParams.put(newParam.getKey(), newParam);
			return this;
		}
		
		
		public Integer getIntForParam(FEParamInt param){
			FEParam par = rawParams.get(param.getKey());
			if(par==null){
				return null;
			}
			return ((FEParamInt)par).getValue();
		}


		public ParameterSheetBuilder setAudioFormat(AudioFormat af){
			setHz((int)af.getSampleRate());
			setBytePerSample(af.getSampleSizeInBits()/8);
			isBigEndian(af.isBigEndian());
			return this;
		}
		public ParameterSheetBuilder setHz(int hz){
			return setParameter(FEParam.HZ, hz);
		}
		public ParameterSheetBuilder setBytePerSample(int bytePerSample) {
			return setParameter(FEParam.BYTE_PER_SAMPE, bytePerSample);
		}
		public ParameterSheetBuilder isBigEndian(boolean b){
			return setParameter(FEParam.IS_BIG_ENDIAN, b);
		}
		public ParameterSheetBuilder setFrameLenInMs(int frameLenInMs){
			setParameter(FEParam.FRAME_LEN_MS, frameLenInMs);
			setParameter(FEParam.FRAME_SHIFT_MS, frameLenInMs/2);
			
			Integer hz = getIntForParam(FEParam.HZ);
			if(hz!=null){
				int sampleLen = (frameLenInMs*hz)/1000;
				setParameter(FEParam.FRAME_LEN_SAMPLE, sampleLen);
				Integer byteLen = getIntForParam(FEParam.BYTE_PER_SAMPE);
				if(byteLen!=null){
					setParameter(FEParam.FRAME_LEN_BYTE, sampleLen*byteLen);
				}
			}
			return this;
		}
		public ParameterSheetBuilder setFrameShiftInMs(int frameShiftInMs) {
			return setParameter(FEParam.FRAME_LEN_MS, frameShiftInMs);
		}
		public ParameterSheetBuilder setWinType(WindowType winType){
			return setParameter(FEParam.WINDOW_TYPE, winType);
		}
		public ParameterSheet build(){
			return new ParameterSheet(rawParams);
		}
	}
}





