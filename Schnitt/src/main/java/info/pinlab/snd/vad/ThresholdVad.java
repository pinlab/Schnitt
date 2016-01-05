package info.pinlab.snd.vad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.snd.fe.DoubleFrame;
import info.pinlab.snd.trs.DoubleFrameTier;

/**
 * 
 * Create 
 * 
 * 
 * @author Gabor Pinter
 *
 */
public class ThresholdVad implements VAD{
	public static Logger LOG = LoggerFactory.getLogger(ThresholdVad.class);
	
	public static VadParamDouble THRESH = new VadParamDouble("THRESH", 0.8, ThresholdVad.class);
	public static VadParamString THRESH_TARG    = new VadParamString("THRESH_TARG", "amp", ThresholdVad.class);
	public static VadParamBoolean THRESH_FILTER_LOW = new VadParamBoolean("THRESH_FILTER_LOW", true, ThresholdVad.class);
	
	
	private double thresh = THRESH.getValue();
	private String attrib = THRESH_TARG.getValue();
	private boolean isFilterOver = THRESH_FILTER_LOW.getValue();
	private DoubleFrameTier frameTier;
	
	public ThresholdVad(){}
	
	

	@Override
	public <T> VAD setParam(VadParam<T> param, T value){
		if(param==null){
			throw new IllegalArgumentException("Argument (param) can't be null!");
		}
		if(value==null){
			LOG.error("Null param value for  '" + param + "' (does nothing)");
			return this;
		}
		
		String key = param.getKey();
		if(THRESH.getKey().equals(key)){
			thresh = (double)value;
			LOG.info("Param set '" + param + "' > " + value);
		}else if(THRESH_TARG.getKey().equals(key)){
			attrib = (String) value; 
			LOG.info("Param set '" + param + "' > '" + value+"'");
		}else if(THRESH_FILTER_LOW.getKey().equals(key)){
			isFilterOver = (boolean) value;
			LOG.info("Param set '" + param + "' > " + value);
		}else{
			LOG.error("Param not for this VAD '" + param + "'");
		}
		return this;
	}

	
	
	@Override
	public Double getParam(VadParamDouble param) {
		if(param==null){
			throw new IllegalArgumentException("Argument (param) can't be null!");
		}
		String key = param.getKey();
		if(THRESH.getKey().equals(key)){
			return thresh;
		}else{
			LOG.error("Param not for this VAD '" + param + "'");
		}
		return null;
	}

	@Override
	public Boolean getParam(VadParamBoolean param) {
		if(param==null){
			throw new IllegalArgumentException("Argument (param) can't be null!");
		}
		String key = param.getKey();
		if(THRESH_FILTER_LOW.getKey().equals(key)){
			return isFilterOver;
		}else{
			LOG.error("Param not for this VAD '" + param + "'");
		}
		return null;
	}
	@Override
	public String getParam(VadParamString param) {
		if(param==null){
			throw new IllegalArgumentException("Argument (param) can't be null!");
		}
		String key = param.getKey();
		if(THRESH_TARG.getKey().equals(key)){
			return attrib ; 
		}else{
			LOG.error("Param not for this VAD '" + param + "'");
		}
		return null;
	}
	
	
	public ThresholdVad setFrameTier(DoubleFrameTier tier){
		frameTier = tier;
		return this;
	}
	
	public BinaryHypoTier getHypo(){
		BinaryHypoTier hypo = new BinaryHypoTier();
		hypo.addInterval(frameTier.getStartT(), frameTier.getEndT(), false);
		double frameLen = frameTier.getFrameLenInSec();
//		double mark = 0;
//		boolean markVal = false;
		for(Double t0 : frameTier.getTimeLabels()){
			DoubleFrame frame = frameTier.getFrameAt(t0);
			Double attribVal = frame.getArray(attrib)[0];
//			System.out.println("Attrib val " + attribVal);
			//TODO: for high-filter
//			System.out.println(" " + t0 + " " + attribVal + " ? " + thresh + " : " + (attribVal>=thresh) );
			if(attribVal>=thresh){
				//TODO: very inaffecte!
				hypo.addInterval(t0, t0+frameLen, true);
			}
		}
		return hypo;
	}







}



