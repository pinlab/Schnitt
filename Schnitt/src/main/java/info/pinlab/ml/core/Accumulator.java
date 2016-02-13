package info.pinlab.ml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Accumulates data.
 * 
 * @author Gabor Pinter
 * 
 * @param <LABEL_TYPE> type of label,  e.g. Binary for binary classification, String for phonemes...
 * @param <DATA_TYPE> type of data to accumulate (e.g. FeatureVectors or DoubleFrames)
 */
public class Accumulator<LABEL_TYPE, DATA_TYPE> implements Iterable<DATA_TYPE> {
	private final LABEL_TYPE label;
	private final int dim;
	private final List<DATA_TYPE> data;
	
	public Accumulator(LABEL_TYPE label, int dim){
		this.label = label;
		this.dim = dim;
		data = new ArrayList<DATA_TYPE>();
	}

	public void add(DATA_TYPE vec){
//		if(vec.size() != dim)
//			throw new IllegalArgumentException("Feat.vec. must be of size "+ dim + ", not " + vec.size());
		
		data.add(vec);
	}

	public LABEL_TYPE getLabel(){
		return label;
	}

	public int getDim(){
		return dim;
	}
	
	public int size(){
		return data.size();
	}
	
	
	@Override
	public Iterator<DATA_TYPE> iterator() {
		return data.iterator();
	}
	
	
}
