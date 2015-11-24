package info.pinlab.snd.trs;

import java.util.List;
import java.util.TreeMap;

import info.pinlab.snd.vad.VadError;

/**
 * 
 * Holds error data for tiers.
 * 
 * @author Gabor PINTER
 *
 */
public class VadErrorTier extends AbstractIntervalTier<VadError> {
	BinaryTier target = null;
	BinaryTier hypo = null;
	
	private static final int NUM_OF_ARCS = 4;
	private static final int TARG_ON  = 1;
	private static final int TARG_OFF = 2;
	private static final int HYPO_ON    = 3;
	private static final int HYPO_OFF   = 4;
//	private static final int BOTH_ON    = 5;
//	private static final int BOTH_OFF   = 6;
//	private static final int TARG_ON_HYP_OFF  = 7;
//	private static final int TARG_OFF_HYP_ON  = 8;

	private static final int NUM_OF_STATES = 6;
	private static int SIL   = 0;
	private static int VOICE = 1;
	private static int FP_1  = 2;
	private static int FP_2  = 3;
	private static int FN_1  = 4;
	private static int FN_2  = 5;
	
	private static final int[][] VadErrorFsa = new int[NUM_OF_STATES][NUM_OF_ARCS];
	private static final VadError[][] VadErrorType = new VadError[NUM_OF_STATES][NUM_OF_ARCS];
	
	
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
		VadErrorType[FN_1][TARG_OFF] = VadError.WC; 
		VadErrorType[FN_1][HYPO_ON]  = VadError.FEC; 
		VadErrorType[FN_2][TARG_OFF] = VadError.REC; 
		VadErrorType[FN_2][HYPO_ON]  = VadError.MSC; 
		
		VadErrorType[FP_1][TARG_ON]  = VadError.HEAD; 
		VadErrorType[FP_1][HYPO_OFF] = VadError.NDS_1; 
		VadErrorType[FP_2][TARG_ON]  = VadError.NDS_2; 
		VadErrorType[FP_2][HYPO_OFF] = VadError.TAIL;
	}
	
	
	
	
	
	public <T> VadErrorTier(BinaryTier target, BinaryTier hypo){
		//-- conflate tiers
		
		this.target = target;
		this.hypo = hypo;
		
		TreeMap<Double, Integer> conflatedTier = new TreeMap<Double, Integer>(); 
		
		Interval<Boolean> inter=null; 
		for(int i = 0; i < target.size();i++){
			inter = target.getIntervalX(i);
			conflatedTier.put(inter.startT, inter.label ? TARGET_ON : TARGET_OFF);
		}
//		if(inter!=null){
//			conflatedTier.put(inter.endT, TARGET_OFF);
//		}
		
		
		for(int i = 0; i < hypo.size() ;i++){
			inter = hypo.getIntervalX(i);
			Integer targetPt = conflatedTier.get(inter.startT);
			if(targetPt==null){
				conflatedTier.put(inter.startT, inter.label ? HYPO_ON : HYPO_OFF);
			}else{
				switch (targetPt) {
				case TARGET_ON:
					conflatedTier.put(inter.startT, inter.label ? BOTH_ON : TARG_ON_HYP_OFF);
					break;
				case TARGET_OFF:
					conflatedTier.put(inter.startT, inter.label ? TARG_OFF_HYP_ON : BOTH_OFF);
					break;
				default:
					break;
				}
			}
		}
		for(Double key: conflatedTier.keySet()){
			System.out.println(key +"\t" + conflatedTier.get(key));
		}
	}
	
	public void setTargetTier(BinaryTier tier){
		this.target = tier;
	}
	public void setHypoTier(BinaryTier tier){
		this.hypo = tier;
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
