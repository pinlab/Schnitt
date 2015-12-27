package info.pinlab.snd.dsp;

public class DoubleFrame {
	int startSampleId;
	int nextId;
	int prevId;
	
	final double [] samples;
	double[] features; 
	
	DoubleFrame(double [] s, int startSampleId){
		samples = s;
	}
	public int getDim(){
		return samples.length;
	}
	
	public double[] getSamples(){
		return samples;
	}
	
	public void setFeatrues(double [] feat){
		features = feat;
	}
	
	public double [] getFeatrues(){
		return features;
	}
	
	
	
}
