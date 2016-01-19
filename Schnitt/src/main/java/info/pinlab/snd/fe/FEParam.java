package info.pinlab.snd.fe;

import info.pinlab.ml.core.Param;
import info.pinlab.snd.fe.Windower.WindowType;

/**
 * Parameters for Acoustic Front End.
 * 
 * @author Gabor Pinter
 *
 */
public abstract class FEParam<T> extends Param<T>{
	//-- default values:
	public static final FEParamInt HZ = new FEParamInt("HZ", 16000, FEParam.class); ;
	public static final FEParamInt BYTE_PER_SAMPE = new FEParamInt("BYTE_PER_SAMPE", 2, FEParam.class);; 
	public static final FEParamInt FRAME_LEN_MS = new FEParamInt("FRAME_LEN_MS", 20, FEParam.class); ;
	public static final FEParamInt FRAME_LEN_SAMPLE = new FEParamInt("FRAME_LEN_SAMPLE", 
			(FRAME_LEN_MS.getValue()*HZ.getValue())/1000, FEParam.class); ;
	public static final FEParamInt FRAME_LEN_BYTE = new FEParamInt("FRAME_LEN_BYTE",
			FRAME_LEN_MS.getValue()*BYTE_PER_SAMPE.getValue(), FEParam.class); ;
	public static final FEParamInt FRAME_SHIFT_MS = new FEParamInt("FRAME_SHIFT_MS",
			FRAME_LEN_MS.getValue()/2, FEParam.class); ;
	
	public static final FEParamBool IS_BIG_ENDIAN = new FEParamBool("IS_BIG_ENDIAN", false, FEParam.class);
	public static final FEParamObj<WindowType> WINDOW_TYPE = new FEParamObj<WindowType>("WINDOW_TYPE", WindowType.HANNING, FEParam.class);

	public static final FEParamString HANDLE_NAME = new FEParamString("HANDLE_NAME", null, FEParam.class);

	
	/**
	 * Holds list of {@link FrameProcessor}s, separated by ':' <br>
	 * For example,<br> "'nfo.pinlab.snd.fe.HanningWindower:info.pinlab.snd.fe.Fft' 
	 */
	public static final FEParamString FRAME_PROCESSORS = new FEParamString("FRAME_PROCESSORS", null, FEParam.class);

	public FEParam(String key, T value, Class<?> parent) {
		super(key, value, parent);
	}
	
	
}

