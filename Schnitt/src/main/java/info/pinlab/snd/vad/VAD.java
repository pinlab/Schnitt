package info.pinlab.snd.vad;

import info.pinlab.snd.trs.DoubleFrameTier;

public interface VAD {
	public VAD setFrameTier(DoubleFrameTier tier);
	
	public <T> VAD setParam(VadParam<T> param, T value);
	
	public Double getParam(VadParamDouble param);
	public Boolean getParam(VadParamBoolean param);
	public String getParam(VadParamString param);

	
	public BinaryHypoTier getHypo();
	
	

}
