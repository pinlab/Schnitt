package info.pinlab.snd.dsp;

public class FEParamBool extends FEParam{
	private boolean value;

	/**
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 */
	public FEParamBool(String key, boolean value, Class<?> clazz){
		super(key, clazz);
		this.value = value;
	}
	
	public boolean getValue(){
		return value;
	}
}
