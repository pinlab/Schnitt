package info.pinlab.snd.vad;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.Interval;
import info.pinlab.snd.trs.IntervalTier;

public class VadErrorTierTest {

	

	
	@Test 
	public void testPerfect(){
		//-- targ: -----+++++++++-----
		//-- hypo: -----+++++++++-----
		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0, 3, false)
			 	.addInterval(1, 2, true);
		IntervalTier<Boolean>  hypo = new BinaryTier()
				.addInterval(1, 2, true);

		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);

		assertTrue(vadTier.size()==3);
		
		Interval<VadError> interval_0 = vadTier.getIntervalX(0);
		Interval<VadError> interval_1 = vadTier.getIntervalX(1);
		Interval<VadError> interval_2 = vadTier.getIntervalX(2);
		
		assertTrue(interval_0.startT == 0.0d);
		assertTrue(interval_1.startT == 1.0d);
		assertTrue(interval_2.startT == 2.0d);
		assertTrue(VadError.TN  .equals(interval_0.label));
		assertTrue(VadError.TP  .equals(interval_1.label));
		assertTrue(VadError.TN  .equals(interval_2.label));
		
	}
	

	@Test 
	public void testWC(){
		//-- word clip
		//-- targ: -----+++++++++-----
		//-- hypo: -------------------
		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0, 3, false)
			 	.addInterval(1, 2, true);
		IntervalTier<Boolean>  hypo = new BinaryTier();

		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);
		
		assertTrue(vadTier.size()==3);
		
		Interval<VadError> interval_0 = vadTier.getIntervalX(0);
		Interval<VadError> interval_1 = vadTier.getIntervalX(1);
		Interval<VadError> interval_2 = vadTier.getIntervalX(2);
		
		assertTrue(interval_0.startT == 0.0d);
		assertTrue(interval_1.startT == 1.0d);
		assertTrue(interval_2.startT == 2.0d);
		assertTrue(interval_2.endT   == 3.0d);
		assertTrue(VadError.TN  .equals(interval_0.label));
		assertTrue(VadError.WC  .equals(interval_1.label));
		assertTrue(VadError.TN  .equals(interval_2.label));
		
	}
	
	
	@Test 
	public void testNds1(){
		//-- noise detected as speech
		//-- targ: -------------------
		//-- hypo: -----+++++++++----- 
		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0,3,false);
		IntervalTier<Boolean>  hypo = new BinaryTier()
				.addInterval(1, 2, true);

		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);
		
		assertTrue(vadTier.size()==3);
		
		Interval<VadError> interval_0 = vadTier.getIntervalX(0);
		Interval<VadError> interval_1 = vadTier.getIntervalX(1);
		Interval<VadError> interval_2 = vadTier.getIntervalX(2);
		
		assertTrue(interval_0.startT == 0.0d);
		assertTrue(interval_1.startT == 1.0d);
		assertTrue(interval_2.startT == 2.0d);
		assertTrue(interval_2.endT   == 3.0d);
		assertTrue(VadError.TN  .equals(interval_0.label));
		assertTrue(VadError.NDS_1  .equals(interval_1.label));
		assertTrue(VadError.TN  .equals(interval_2.label));
	}
	
	
	@Test 
	public void testFecRec(){
		//-- Front End Clip & Rear end clip
		//-- targ: -----+++++++++-----
		//-- hypo: ---+++++++++++++---
		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0, 3, false)
			 	.addInterval(1, 2, true);
		IntervalTier<Boolean>  hypo = new BinaryTier()
				.addInterval(1.25, 1.75, true);

		
		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);
		assertTrue(vadTier.size()==5);
		
		Interval<VadError> interval_0 = vadTier.getIntervalX(0);
		Interval<VadError> interval_1 = vadTier.getIntervalX(1);
		Interval<VadError> interval_2 = vadTier.getIntervalX(2);
		Interval<VadError> interval_3 = vadTier.getIntervalX(3);
		Interval<VadError> interval_4 = vadTier.getIntervalX(4);
		
		assertTrue(interval_0.startT == 0.0d);
		assertTrue(interval_1.startT == 1.0d);
		assertTrue(interval_2.startT == 1.25d);
		assertTrue(interval_3.startT == 1.75d);
		assertTrue(interval_4.startT == 2.0d);
		assertTrue(interval_4.endT == 3.0d);
		assertTrue(VadError.TN  .equals(interval_0.label));
		assertTrue(VadError.FEC .equals(interval_1.label));
		assertTrue(VadError.TP  .equals(interval_2.label));
		assertTrue(VadError.REC .equals(interval_3.label));
		assertTrue(VadError.TN  .equals(interval_4.label));

	}
	
	
	@Test 
	public void testHeadTail(){
		//-- targ: -----+++++++++-----
		//-- hypo: ---+++++++++++++---
		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0, 3, false)
			 	.addInterval(1, 2, true);
		IntervalTier<Boolean>  hypo = new BinaryTier()
				.addInterval(0.5, 2.5, true);

		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);
		
		assertTrue(vadTier.size()==5);
		Interval<VadError> interval_0 = vadTier.getIntervalX(0);
		Interval<VadError> interval_1 = vadTier.getIntervalX(1);
		Interval<VadError> interval_2 = vadTier.getIntervalX(2);
		Interval<VadError> interval_3 = vadTier.getIntervalX(3);
		Interval<VadError> interval_4 = vadTier.getIntervalX(4);
		
		assertTrue(interval_0.startT == 0.0d);
		assertTrue(interval_1.startT == 0.5d);
		assertTrue(interval_2.startT == 1.0d);
		assertTrue(interval_3.startT == 2.0d);
		assertTrue(interval_4.startT == 2.5d);
		assertTrue(interval_4.endT == 3.0d);
		assertTrue(VadError.TN  .equals(interval_0.label));
		assertTrue(VadError.HEAD.equals(interval_1.label));
		assertTrue(VadError.TP  .equals(interval_2.label));
		assertTrue(VadError.TAIL.equals(interval_3.label));
		assertTrue(VadError.TN  .equals(interval_4.label));
	}
	
	
	@Test 
	public void testAlmostAllErrors(){
		//-- noise detected as speech
		//-- targ: -----++++++++++-------++--------
		//-- hypo: ---+++++----+++++++-------+++---

		IntervalTier<Boolean> targ = new BinaryTier()
				.addInterval(0, 5, false)
			 	.addInterval(1, 2, true)
				.addInterval(3, 4, true);
		IntervalTier<Boolean>  hypo = new BinaryTier()
				.addInterval(0.5, 1.5, true)
				.addInterval(1.8, 2.2, true)
				.addInterval(4.2, 4.5, true)
				;
		
		VadErrorTier vadTier = new VadErrorTier((BinaryTier)targ, (BinaryTier)hypo);
//		System.out.println(vadTier);
		
		assertTrue(vadTier.size()==11);
		int i=0;
		Interval<VadError> interval_00 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_01 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_02 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_03 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_04 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_05 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_06 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_07 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_08 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_09 = vadTier.getIntervalX(i++);
		Interval<VadError> interval_10 = vadTier.getIntervalX(i++);
		assertTrue(interval_00.startT == 0.0d);
		assertTrue(interval_01.startT == 0.5d);
		assertTrue(interval_02.startT == 1.0d);
		assertTrue(interval_03.startT == 1.5d);
		assertTrue(interval_04.startT == 1.8d);
		assertTrue(interval_05.startT == 2.0d);
		assertTrue(interval_06.startT == 2.2d);
		assertTrue(interval_07.startT == 3.0d);
		assertTrue(interval_08.startT == 4.0d);
		assertTrue(interval_09.startT == 4.2d);
		assertTrue(interval_10.startT == 4.5d);
		assertTrue(interval_10.endT == 5.0d);
		assertTrue(VadError.TN   .equals(interval_00.label));
		assertTrue(VadError.HEAD .equals(interval_01.label));
		assertTrue(VadError.TP   .equals(interval_02.label));
		assertTrue(VadError.MSC  .equals(interval_03.label));
		assertTrue(VadError.TP   .equals(interval_04.label));
		assertTrue(VadError.TAIL .equals(interval_05.label));
		assertTrue(VadError.TN   .equals(interval_06.label));
		assertTrue(VadError.WC   .equals(interval_07.label));
		assertTrue(VadError.TN   .equals(interval_08.label));
		assertTrue(VadError.NDS_1.equals(interval_09.label));
		assertTrue(VadError.TN   .equals(interval_10.label));
	}
	

}
