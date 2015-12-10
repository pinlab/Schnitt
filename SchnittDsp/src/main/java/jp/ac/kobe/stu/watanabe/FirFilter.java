package jp.ac.kobe.stu.watanabe;

/**
 * Executing fir filter
 * 
 * @author snoopy
 *
 */

public class FirFilter {
	final double[] coef;
	static int[] samples;

	/**
	 * Setting the coefficient
	 * 
	 * @param coef
	 */
	public FirFilter(double[] coef) {
		this.coef = coef;
	}

	/**
	 * Execute fir filter
	 * 
	 * @param int
	 *            []sample
	 * @return double [] fir-filtered array
	 */
	public double[] doFirFilter(int[] sample) {
		samples = sample;

		double[] filteredArr = new double[samples.length];
		for (int n = 0; n < samples.length; n++) {
			for (int i = 0; i < coef.length; i++) {
				if (n - i >= 0) {
					double value = coef[i] * samples[n - i];
					filteredArr[n] += value;
				}
			}
		}

		return filteredArr;

	}

}
