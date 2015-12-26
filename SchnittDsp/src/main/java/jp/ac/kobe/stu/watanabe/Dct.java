package jp.ac.kobe.stu.watanabe;

/**
 * Execute Discrete cosine transform
 * 
 * @author snoopy
 * 
 *
 */
public class Dct {
	private int N;
	private double[][] D;

	/**
	 * setting the length of a data set
	 * 
	 * @param int
	 *            dataLength
	 */
	public Dct(int dataLength) {
		this.N = dataLength;
		D = new double[N][N];

		for (int i = 0; i < N; i++) {
			double k = i == 0 ? 1.0 / Math.sqrt(2.0) : 1.0;
			for (int j = 0; j < N; j++) {
				D[i][j] = Math.sqrt(2.0 / N) * k * Math.cos(i * (j + 0.5) * Math.PI / (double) N);
			}
		}
	}

	/**
	 * Execute dct
	 * 
	 * @param double
	 *            []data
	 * @return double [] transformed array
	 */
	public double[] transform(double[] data) {
		double[] dctArr = new double[N];

		for (int i = 0; i < N; i++) {
			double sumOf = 0;
			for (int j = 0; j < N; j++) {
				double value = D[i][j] * data[j];
				sumOf += value;
			}

			dctArr[i] = sumOf;
		}

		return dctArr;
	}
}
