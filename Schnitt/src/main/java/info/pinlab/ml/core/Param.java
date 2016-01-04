package info.pinlab.ml.core;

public abstract class Param<T> {
	private String key;
	private Class<?>parent;
	private T value;
	
	public Param (String key, T value, Class<?> parent){
		this.key = key;
		this.parent = parent;
                this.value = value;
	}
	
	public String getKey(){
		return key;
	}
	
	/**
	 * Gets full qualified name.
	 * 
	 * @return
	 */
	public String getFqName(){
		return parent.getName() +"." +key;
	}
	
	public Class<?> getParentClazz(){
		return parent;
	}
	
	
	
	public T getValue(){
		return value;
	}

	@Override
	public String toString(){
		return parent.getName()+"."+key+"="+value;
	}
	
}
