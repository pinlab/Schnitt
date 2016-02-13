package info.pinlab.ml.core;

public class FeatureVector{
	public final double [] feats;
	public final int id;
	
	public FeatureVector(int id, double [] features){
		this.feats = features;
		this.id = id;
	}
	
	public int size(){
		return feats.length;
	}
}
