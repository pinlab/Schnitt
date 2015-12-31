package info.pinlab.snd.dsp;

public class FEParamInt extends FEParam{
	private int value;

	/**
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 */
	public FEParamInt (String key, int value, Class<?> clazz){
		super(key, clazz);
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
}
