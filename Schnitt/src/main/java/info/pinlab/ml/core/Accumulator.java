package info.pinlab.ml.core;

/**
 * Accumulates data
 * 
 * @author Gabor Pinter
 *
 */
public interface Accumulator<T> {
	public void add(T data);
	public int size();
}
