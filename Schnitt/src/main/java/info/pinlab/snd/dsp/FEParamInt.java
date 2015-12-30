package info.pinlab.snd.dsp;

public class FEParamInt extends FEParam{
	private String key;
	private int value;
	private Class<?>clazz;
	
	
	/**
	 * 
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 */
	public FEParamInt (String key, int value, Class<?> clazz){
		this.key = key;
		this.value = value;
		this.clazz = clazz;
	}
	
	public int getInt(){
		return value;
	}
	public String getKey(){
		return key;
	}
	public Class<?> getClazz(){
		return clazz;
	}
	
}
