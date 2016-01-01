package info.pinlab.snd.fe;

public class PreEmphasize extends AbstractFrameProcessor {
	public static final FEParamInt HZ = new FEParamInt("HZ", 16000, PreEmphasize.class);
	public static final FEParamDouble PREEMPH_COEF = new FEParamDouble("PREEMPH_COEF", 16000, PreEmphasize.class);
	
	private double preEmphCoef;
	private double [] preEmphCoefArr;
	private double [] preEmphedArr = null;
	

	public PreEmphasize(ParamSheet context) {
		super(context);
	}
	
	@Override
	public String getKey() {
		return "preEmph";
	}
	

	@Override
	public void init() {
		preEmphCoef = context.get(PREEMPH_COEF);
		preEmphCoefArr = new double []{ 1.0, - preEmphCoef}; // transfer the sign of PreEmphCoef to minus.
	}

	@Override
	public double[] process(double[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < preEmphCoefArr.length; j++) {
				if (i - j >= 0) {
					preEmphedArr[j] += preEmphCoefArr[j] * arr[i - j];
				}
			}
		}
		return preEmphedArr;
	}
}
