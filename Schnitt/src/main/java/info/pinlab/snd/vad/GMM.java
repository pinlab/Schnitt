package info.pinlab.snd.vad;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

/**
 * Gaussian Mixture Model
 * 
 * @author Gabor Pinter
 *
 */
public class GMM {

	
	MultivariateNormalDistribution mnd;
	//http://melodi.ee.washington.edu/people/bilmes/mypapers/em.pdf
	
	public GMM(double [] means, double [][] covariances){
		mnd = new MultivariateNormalDistribution(means, covariances);
	}
	

	
	public static void main(String[] args) {
	}
}
