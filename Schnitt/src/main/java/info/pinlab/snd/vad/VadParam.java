package info.pinlab.snd.vad;

public class VadParam<T> {
	final String name;  //--to use in ini file
	String label; //-- for example on GUI

	T minT;
	T maxT;
	T defaultT;
	T value;
	final Class<T>clazz;

	public VadParam(String name, T value, Class<T> cls){
		this.name = name;
		this.label = name;
		this.value = value;
		clazz =cls;
	}
	public VadParam(String name, Class<T> cls){
		this.name = name;
		clazz =cls;
	}


	public void setParamVal(T value){
		this.value = value;
	}
	public String getParamName(){
		return name;
	}
	public T getValue(){
		return value;
	}
	
	
	
	public void setMinVal(T value){
		this.minT = value;
	}
	public void setMaxVal(T value){
		this.maxT = value;
	}
	public void setDefaultVal(T value){
		this.defaultT = value;
	}
	public void setLabel(String s){
		this.label = s;
	}
}
