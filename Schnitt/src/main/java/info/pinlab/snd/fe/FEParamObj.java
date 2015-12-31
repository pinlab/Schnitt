package info.pinlab.snd.fe;

public class FEParamObj <T> extends FEParam<Object> {

	FEParamObj(String key, T value, Class<?> clazz) {
		super(key, value, clazz);
	}
}
