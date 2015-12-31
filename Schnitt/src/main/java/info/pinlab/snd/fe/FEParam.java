package info.pinlab.snd.fe;

import info.pinlab.snd.fe.Windower.WindowType;

/**
 * Parameters for Acoustic Front End.
 * 
 * @author Gabor Pinter
 *
 */
public abstract class FEParam<T>{
	private String key;
	private Class<?>clazz;
	private T value;
	
	//-- default values:
	public static final FEParamInt HZ ;
	public static final FEParamInt BYTE_PER_SAMPE ; 
	public static final FEParamInt FRAME_LEN_MS ;
	public static final FEParamInt FRAME_LEN_SAMPLE ;
	public static final FEParamInt FRAME_LEN_BYTE ;
	public static final FEParamInt FRAME_SHIFT_MS ;
	
	public static final FEParamBool IS_BIG_ENDIAN = new FEParamBool("IS_BIG_ENDIAN", false, FEParam.class);
	public static final FEParamObj<WindowType> WINDOW_TYPE = new FEParamObj<WindowType>("WINDOW_TYPE", WindowType.HANNING, FEParam.class);
	
	static{
		int hz = 16000;
		int frameLenMs = 20;
		int bytePerSample = 2;
		HZ = new FEParamInt("HZ", hz, FEParam.class);
		BYTE_PER_SAMPE = new FEParamInt("BYTE_PER_SAMPE", 2, FEParam.class);
		FRAME_LEN_MS = new FEParamInt("FRAME_LEN_MS", frameLenMs, FEParam.class);
		FRAME_LEN_SAMPLE = new FEParamInt("FRAME_LEN_SAMPLE", 
				(frameLenMs*hz)/1000, FEParam.class);
		FRAME_LEN_BYTE = new FEParamInt("FRAME_LEN_BYTE",
				frameLenMs*bytePerSample, FEParam.class);
		FRAME_SHIFT_MS = new FEParamInt("FRAME_LEN_BYTE",
				frameLenMs/2, FEParam.class);
	}
	
	
	FEParam (String key, T value, Class<?> clazz){
		this.key = key;
		this.clazz = clazz;
	}
	
	public String getKey(){
		return key;
	}
	
	public Class<?> getClazz(){
		return clazz;
	}
	
	public T get(){
		return value;
	}
	
}
