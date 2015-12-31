package info.pinlab.snd.fe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DoubleFrame implements Frame {
	long startSampleIx;
	long startT;
	int nextId;
	int prevId;
	
	private final Map<String, double []> data;
	
	
	static double [] zeros(int len){
		return new double[len];
	}
	static double[] ones(int len){
		double [] arr = new double[len];
		Arrays.fill(arr, 1);
		return arr;
	}

	
	
	public DoubleFrame(double [] s, String key, long startSampleIx){
		data = new HashMap<String, double[]>();
		data.put(key, s);
		this.startSampleIx = startSampleIx;
	}
	
	
	public void setStartSempleIx(long ix){
		startSampleIx = ix;
	}
	public void setStartSampleT(long t){
		startT = t;
	}
	
	public double [] get(String key){
		return data.get(key);
	}
	
	
	public void put(double [] arr, String key){
		data.put(key, arr);
	}
	@Override
	public long getStartSampleIx() {
		return startSampleIx;
	}
}

