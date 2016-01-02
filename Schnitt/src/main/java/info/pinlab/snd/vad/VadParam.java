package info.pinlab.snd.vad;

import info.pinlab.ml.core.Param;


/**
 * 
 * 
 * 
 * @author Gabor Pinter
 *
 * @param <T>
 */
public class VadParam<T> extends Param<T>{

	public VadParam(String key, T value, Class<?> parent) {
		//-- full quialified class+field names are keys:  
		//-- : avoiding sharing of parameters 
		super(parent.getName()+"." + key, value, parent);
	}
	
//	final String key;  //--to use in ini file
//	String label; //-- for example on GUI
//
//	T minT;
//	T maxT;
//	T defaultT;
//	T value;
//	final Class<T> parent;
//
//	public VadParam(String name, T value, Class<T> parent){
//		this.key = name;
//		this.label = name;
//		this.value = value;
//		clazz =cls;
//	}
//	public VadParam(String name, Class<T> cls){
//		this.key = name;
//		clazz =cls;
//	}
//
//
//	public void setParamVal(T value){
//		this.value = value;
//	}
//	public String getParamName(){
//		return key;
//	}
//	public T getValue(){
//		return value;
//	}
//	
//	
//	
//	public void setMinVal(T value){
//		this.minT = value;
//	}
//	public void setMaxVal(T value){
//		this.maxT = value;
//	}
//	public void setDefaultVal(T value){
//		this.defaultT = value;
//	}
//	public void setLabel(String s){
//		this.label = s;
//	}
}
