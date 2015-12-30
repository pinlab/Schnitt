package info.pinlab.snd.dsp;

import info.pinlab.snd.dsp.ParameterSheet.ProcessorParameter;
import jdk.nashorn.internal.objects.annotations.Constructor;

public class PreEmphasize extends AbstractFrameProcessor {
	private int hz;
	private int fftN;
	private double preEmphCoef;
	private double [] preEmphCoefArr;
	private double [] preEmphedArr = null;
	
	enum PreEmphasizeParams implements ProcessorParameter{
		PREEMPH_COEF(0.97)
		;
	
	final String id;
	final String label;
	final Object defVal;
	PreEmphasizeParams(Object defVal){
		this.id = FrameProducer.class.getName()+"."+this.toString().toUpperCase();
		//OUTPUT: info.pinlab.snd.dsp.FrameProducer.INFO.PINLAB.SND.DSP.PREEMPHASIZE@xxxx
		this.label= id.toUpperCase();
		this.defVal = defVal;
	}
	
		@Override
		public String getUniqName() {return id;		}
		@Override
		public String getLabel() {	 return label;		}
		@Override
		public boolean getBoolean() {return (boolean)defVal; }
		@Override
		public int getInt() {		 return (int) defVal;			}
		@Override
		public double getDouble() {  return (double) defVal;			}
		@Override
		public Object get() {	 	return defVal;		}
	}
	
	public static ProcessorParameter[] getProcessorParams(){
		return PreEmphasizeParams.values();
	}


	public PreEmphasize(ParameterSheet context) {
		super(context);
	}
	
	@Override
	public String getKey() {
		return "preEmph";
	}
	
	@Override
	public String getPredecessorKey() {
		return null;
	}


	@Override
	public void init() {
		preEmphCoef = context.getInt(PreEmphasizeParams.PREEMPH_COEF);
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
