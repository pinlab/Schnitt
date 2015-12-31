package info.pinlab.snd.dsp;

public class FEParamObj <T> extends FEParam {
	private T value;
	FEParamObj(String key, T value, Class<?> clazz) {
		super(key, clazz);
		this.value = value;
	}
	
	public T getValue(){
		return value;
	}

}
