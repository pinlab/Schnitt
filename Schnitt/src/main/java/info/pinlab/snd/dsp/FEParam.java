package info.pinlab.snd.dsp;

import info.pinlab.snd.dsp.Windower.WindowType;

/**
 * Parameters for Acoustic Front End.
 * 
 * @author Gabor Pinter
 *
 */
public class FEParam{
	private String key;
	private Class<?>clazz;

	//-- default values:
	public static final FEParamInt HZ = new FEParamInt("HZ", 16000, FEParam.class);
	public static final FEParamInt BYTE_PER_SAMPE = new FEParamInt("BYTE_PER_SAMPE", 2, FEParam.class); 
	public static final FEParamInt FRAME_LEN_MS = new FEParamInt("FRAME_LEN_MS", 20, FEParam.class);
	public static final FEParamInt FRAME_LEN_SAMPLE = new FEParamInt("FRAME_LEN_SAMPLE", 
			(FRAME_LEN_MS.getValue()*HZ.getValue())/1000, FEParam.class);
	public static final FEParamInt FRAME_LEN_BYTE = new FEParamInt("FRAME_LEN_BYTE",
			FRAME_LEN_MS.getValue()* BYTE_PER_SAMPE.getValue(), FEParam.class);
	public static final FEParamInt FRAME_SHIFT_MS = new FEParamInt("FRAME_LEN_BYTE",
			FRAME_LEN_MS.getValue()/2, FEParam.class);
	
	public static final FEParamBool IS_BIG_ENDIAN = new FEParamBool("IS_BIG_ENDIAN", false, FEParam.class);
	public static final FEParamObj<WindowType> WINDOW_TYPE = new FEParamObj<WindowType>("WINDOW_TYPE", WindowType.HANNING, FEParam.class);
	
	FEParam (String key, Class<?> clazz){
		this.key = key;
		this.clazz = clazz;
	}
	
	public String getKey(){
		return key;
	}
	
	public Class<?> getClazz(){
		return clazz;
	}
	
}
