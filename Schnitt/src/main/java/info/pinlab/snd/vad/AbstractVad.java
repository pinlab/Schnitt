package info.pinlab.snd.vad;

public abstract class AbstractVad implements VAD {

	@Override
	public Double getParam(VadParamDouble param) {
		return null;
	}

	@Override
	public Boolean getParam(VadParamBoolean param) {
		return null;
	}

	@Override
	public String getParam(VadParamString param) {
		return null;
	}

}
