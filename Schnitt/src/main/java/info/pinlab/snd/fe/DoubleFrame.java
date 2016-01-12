package info.pinlab.snd.fe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoubleFrame implements Frame {
	private long startSampleIx;
//	private long startT;
//	private int nextId;
//	private int prevId;
	
	private final Map<String, double []> arrays;
//	private final Map<String, Double> numbers;
	
	
	static double [] zeros(int len){
		return new double[len];
	}
	static double[] ones(int len){
		double [] arr = new double[len];
		Arrays.fill(arr, 1);
		return arr;
	}
	
	public DoubleFrame(double [] s, String key, long startSampleIx){
		arrays = new HashMap<String, double[]>();
		arrays.put(key, s);
		this.startSampleIx = startSampleIx;
//		this.numbers = new HashMap<>();
	}
	
	public void setStartSempleIx(long ix){
		startSampleIx = ix;
	}
//	public void setStartSampleT(long t){
//		startT = t;
//	}
	
	/**
	 * 
	 * @param key the keyword 
	 * @return double array for key, or null if no such array
	 */
	public double [] getArray(String key){
		return arrays.get(key);
	}
//	public Double getNumber(String key){
//		return numbers.get(key);
//	}
	
	public void addArray(String key, double [] arr){
		arrays.put(key, arr);
	}
	
	public void addNumber(String key, double val){
		arrays.put(key, new double[]{val});
	}
	
	
	
	@Override
	public long getStartSampleIx() {
		return startSampleIx;
	}
	
	public List<String> getDataLabels(){
		return new ArrayList<>(arrays.keySet());
	}
	
}

