package info.pinlab.snd.dsp;

import info.pinlab.snd.dsp.ParameterSheet.ProcessorParameter;

public class Mfcc extends AbstractFrameProcessor{
	public static final String MFCC_CH = "MFCC_CH";
	public static final String CEPS_COEF = "CEPS_COEF";
	
	@ParamInt(label=MFCC_CH)
	public int mfccChN = 26; //-- Default value
	@ParamInt(label=CEPS_COEF)
	public int cepsCoef = 12; //-- Default Value
	
	public double [][]transArr;
	public double [] cepsArr;
	public double [] mfccArr;
	public double [] loggedArr;
	
	
	enum DctParam implements ProcessorParameter{
		MFCC_CH(26),
		CEPS_COEF(12)
		;
		
		final private String id;
		final private String label;
		private Object value;
		
		DctParam(Object value){
			this.value = value;
			label = this.name().toUpperCase();
			id = DctParam.class.getName() + "." + label;
		}

		@Override
		public String getUniqName() {
			return id;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public boolean getBoolean() {
			return (boolean) value;
		}

		@Override
		public int getInt() {
			return (int) value;
		}

		@Override
		public double getDouble() {
			return (double) value;
		}

		@Override
		public Object get() {
			return value;
		}
	}
	
	public static ProcessorParameter[] getProcessorParams(){
		return DctParam.values();
	}
	
	public Mfcc(ParameterSheet context) {
		super(context);
	}

	@Override
	public String getPredecessorKey() {
		return "mfc";
	}

	@Override
	public String getKey() {
		return "mfcc";
	}
	
	
	@Override
	public void init() {
		setKey("mfcc");
		setPredecessorKey("mfc");
		
		transArr = new double [mfccChN][mfccChN];
		
		//-- dct formula: c = T * x: c = transformed coef., x = input signal
		for (int i = 0; i < mfccChN; i++) {
			double k = i == 0 ? 1.0 / Math.sqrt(2.0) : 1.0;
			for (int j = 0; j < mfccChN; j++) {
				transArr[i][j] = Math.sqrt(2.0 / mfccChN) * k * Math.cos(i * (j + 0.5) * Math.PI / (double) mfccChN);
			}
		}
	}

	@Override
	public double[] process(double[] arr) {
		// logarithm
		for (int i = 0; i < mfccChN; i++) {
			// Log-transfer Sum of FilteredBanked Amplitude
			loggedArr[i] = Math.log10(arr[i]);
		}
		
		// apply dct
		for (int i = 0; i < mfccChN; i++) {
			double sumOf = 0;
			for (int j = 0; j < mfccChN; j++) {
				sumOf += transArr[i][j] * loggedArr[j];
			}
			cepsArr[i] = sumOf;
		}
		System.arraycopy(cepsArr, 0, mfccArr, 0, mfccChN);
		return mfccArr;
	}
}
