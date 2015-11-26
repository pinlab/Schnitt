package info.pinlab.snd.trs;

import java.util.List;

import info.pinlab.snd.vad.VadError;

/**
 * 
 * Holds error data for tiers.
 * 
 * @author Gabor PINTER
 *
 */
public class VadErrorTier extends AbstractIntervalTier<VadError> {
	IntervalTier<Boolean> target = null;
	IntervalTier<Boolean> hypo = null;
	
	private static final int NUM_OF_ARCS = 4;
	private static final int TARG_ON   = 0;
	private static final int TARG_OFF  = 1;
	private static final int HYPO_ON   = 2;
	private static final int HYPO_OFF  = 3;

	private static final int NUM_OF_STATES = 6;
	private static int SIL   = 0;
	private static int VOICE = 1;
	private static int FP_1  = 2;
	private static int FP_2  = 3;
	private static int FN_1  = 4;
	private static int FN_2  = 5;
	
	static final int[][] VadErrorFsa = new int[NUM_OF_STATES][NUM_OF_ARCS];
	static final VadError[][] VadErrorType = new VadError[NUM_OF_STATES][NUM_OF_ARCS];
	
	
	static{
		//-- Finite State Automaton for VAD errors
		//  arcs:     Targ_on   Targ_off   Hypo_on   Hypo_off
		//  states:                                          
		//  SIL          FN_1                 FP_1           
		//  FN_1                     SIL     VOICE           
		//  FN_2                     SIL     VOICE                //-- FN_1 
		//  FP_1        VOICE                             SIL
		//	FP_2        VOICE                             SIL       	 
		//  VOICE                   FP_2                 FN_2 
		VadErrorFsa[SIL][TARG_ON] = FN_1;
		VadErrorFsa[SIL][HYPO_ON] = FP_1;

		VadErrorFsa[FN_1][TARG_OFF] = SIL;
		VadErrorFsa[FN_1][HYPO_ON]  = VOICE;
		VadErrorFsa[FN_2][TARG_OFF] = SIL;
		VadErrorFsa[FN_2][HYPO_ON]  = VOICE;

		VadErrorFsa[FP_1][TARG_ON]  = VOICE;
		VadErrorFsa[FP_1][HYPO_OFF] = SIL;
		VadErrorFsa[FP_2][TARG_ON]  = VOICE;
		VadErrorFsa[FP_2][HYPO_OFF] = SIL;
		
		VadErrorFsa[VOICE][TARG_OFF] = FP_2;
		VadErrorFsa[VOICE][HYPO_OFF] = FN_2;
		
		//-- VAD FSA : error types
		//  arcs:     Targ_on   Targ_off   Hypo_on   Hypo_off
		//  states:                                          
		//  FN_1                      WC       FEC           
		//  FN_2                    OVER       MSC 
		//  FP_1         HEAD                          NDS_1
		//	FP_2        NDS_2                           OVER       	 
		VadErrorType[SIL][TARG_ON] = VadError.TN; 
		VadErrorType[SIL][HYPO_ON] = VadError.TN; 
		VadErrorType[SIL][TARG_OFF] = VadError.TN; 
		VadErrorType[SIL][HYPO_OFF] = VadError.TN; 

		
		VadErrorType[VOICE][TARG_OFF] = VadError.TP; 
		VadErrorType[VOICE][HYPO_OFF] = VadError.TP; 
		
		VadErrorType[FN_1][TARG_OFF] = VadError.WC; 
		VadErrorType[FN_1][HYPO_ON]  = VadError.FEC; 
		VadErrorType[FP_1][TARG_ON]  = VadError.HEAD; 
		VadErrorType[FP_1][HYPO_OFF] = VadError.NDS_1; 

		VadErrorType[FN_2][TARG_OFF] = VadError.REC; 
		VadErrorType[FN_2][HYPO_ON]  = VadError.MSC; 
		VadErrorType[FP_2][TARG_ON]  = VadError.NDS_2; 
		VadErrorType[FP_2][HYPO_OFF] = VadError.TAIL;
	}
	
	
	
	public <T> VadErrorTier(IntervalTier<Boolean> target, IntervalTier<Boolean> hypo){
		//-- conflate tiers
		
		this.target = target;
		this.hypo = hypo;
		
		int STATE = SIL;

		int targIx, hypoIx;
		targIx=hypoIx=0;
		

		
		VadError err = null;
		double errT = -1;
		double prevErrT = -1;
		
		//-- calculate the first T
		Interval<Boolean> targInterval = target.getIntervalX(0);
		Interval<Boolean> hypoInterval = hypo.getIntervalX(0);

		if(targInterval!=null){
			if(hypoInterval != null){
				if( targInterval.startT < hypoInterval.startT){
					prevErrT = targInterval.startT;
				}else{
					prevErrT = hypoInterval.startT;
				}
			}else{
				prevErrT = targInterval.startT;
			}
		}else{
			if(hypoInterval != null){
				prevErrT = hypoInterval.startT;
			}else{ //-- both NULL!!!
				//-- this cannot be reached!
				LOG.error("Both hypo and target tiers are empty!");
				return;
			}
		}
		errT = prevErrT;
		
		while (true){
			targInterval = target.getIntervalX(targIx);
			hypoInterval = hypo.getIntervalX(hypoIx);

			if(targInterval == null && hypoInterval==null){
				break;
			}
			
			
			if(hypoInterval != null && 
					(targInterval==null || hypoInterval.startT < targInterval.startT)) {
				errT = hypoInterval.startT;
				if(hypoInterval.label != null && hypoInterval.label){//--hypo ON
					err = VadErrorType[STATE][HYPO_ON];
					STATE = VadErrorFsa[STATE][HYPO_ON];
				}else{ //-- hypo off
					err = VadErrorType[STATE][HYPO_OFF];
					if(err!=null){
						super.points.put(hypoInterval.startT, err);
					}
					STATE = VadErrorFsa[STATE][HYPO_OFF];
				}
				hypoIx++;
			}else{
				errT = targInterval.startT;
//				System.out.println(errT);
				if(targInterval.label != null && targInterval.label){
					err = VadErrorType[STATE][TARG_ON];
					STATE = VadErrorFsa[STATE][TARG_ON];
				}else{
					err = VadErrorType[STATE][TARG_OFF];
					STATE = VadErrorFsa[STATE][TARG_OFF];
				}
				targIx++;
			}
			if(err!=null){
				super.points.put(prevErrT, err);
			}
//			double dur = errT-prevErrT;
//			System.out.println(errT + "\t" + "STATE: " + STATE + "\t" + err + "\t" + dur);
			prevErrT = errT;
		}
		if(err!=null){
			super.points.put(prevErrT, err);
		}
		
	}
	
	public void setTargetTier(BinaryTier tier){
		this.target = tier;
	}
	public void setHypoTier(BinaryTier tier){
		this.hypo = tier;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		double prev= -1;
		VadError prevLab = null;
		for(double pt : super.points.keySet()){
			if(pt > 0.0d){
				sb	.append(prev).append(" - ")
					.append(pt).append(" ")
					.append(prevLab)
					.append('\n');
			}
			prev = pt;
			prevLab = super.points.get(pt);
		}
		return sb.toString();
	}

	
	
	/**
	 * 
	 */
	public void calculateError(){
		
	}
	
	@Override
	public VadError combineLabels(List<VadError> labels) {
		return null;
	}

	
	public static void main(String[] args){
		BinaryTier t1 = new BinaryTier();
		BinaryTier t2 = new BinaryTier();
		
		
		t1.addInterval(0.0d, 1.0, false);
		t1.addInterval(1.0d, 2.0, true);
		t1.addInterval(2.0d, 3.0, false);
		
		t2.addInterval(0.0d, 0.9, false);
		t2.addInterval(0.9d, 2.2, true);
		t2.addInterval(2.1d, 3.0, false);
		
		System.out.println(t1);
		System.out.println("\n");
		System.out.println(t2);
		
		
		new VadErrorTier(t1, t2);
		
	}
}
