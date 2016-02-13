package info.pinlab.ml.core;

import java.util.List;

public interface BinaryClassifier extends Classifier<Boolean>{
	
	/**
	 * Start training on set data.
	 * <ul> 
	 * <li> synchronous running (can take long)
	 * <li> start from a different thread
	 * </ul> 
	 */
	public void train();
	
	/**
	 * @param trainData data to train on
	 */
	public void setTrainData(
			Accumulator<Boolean, FeatureVector> acc1, 
			Accumulator<Boolean, FeatureVector> acc2);

	
	/**
	 * @param data
	 * @return
	 * @throws IllegalStateException if called before training ({@code #train()}).
	 */
	public List<Boolean> predictLabel(List<FeatureVector> data) throws IllegalStateException;
}

