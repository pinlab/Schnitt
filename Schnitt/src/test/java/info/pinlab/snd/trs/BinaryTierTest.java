package info.pinlab.snd.trs;
import static org.junit.Assert.*;

import org.junit.Test;


public class BinaryTierTest {


	
	@Test
	public void testAddRemoveSameInterval(){
		BinaryTier tier = new BinaryTier();
		tier.addInterval(0.0, 2.0, true);
		tier.addInterval(0.0, 2.0, false);
		assertTrue(tier.size()==1);
		assertTrue(tier.getIntervalX(0).label==false);
		
		tier = new BinaryTier();
		tier.addInterval(1.0, 2.0, true);
		tier.addInterval(1.0, 2.0, false);
		assertTrue(tier.size()==1);
		assertTrue(tier.getIntervalX(0).label==false);
		
		tier = new BinaryTier();
		tier.addInterval(1.0, 4.0, true);
		tier.addInterval(2.0, 3.0, false);
		tier.addInterval(2.0, 3.0, true);
		assertTrue(tier.size()==2);
		assertTrue(tier.getIntervalX(0).label==false);
		assertTrue(tier.getIntervalX(1).label==true);
//		System.out.println(tier.debugPrint());
		
	}
	
	@Test
	public void testDanglingIntervalsAdded(){
		//  tier:  ---------|
		//  add :               |++++|
		//      :  -------------|++++|
		BinaryTier tier = new BinaryTier();
		tier.addInterval(1.0, 2.0, false);
		tier.addInterval(4.0, 5.0, true);
//		System.out.println(tier.debugPrint());
		assertTrue(tier.getDuration()==5.0d);
		assertTrue(tier.size()==2);
		assertTrue(tier.getIntervalX(0).label==false);
		assertTrue(tier.getIntervalX(1).label==true);


		//  tier:  ++++++++|
		//  add :               |----|
		//      :  ++++++++|---------|
		tier = new BinaryTier();
		tier.addInterval(0.0, 2.0, true);
		tier.addInterval(4.0, 5.0, false);
//		System.out.println(tier.debugPrint());
		assertTrue(tier.getDuration()==5.0d);
		assertTrue(tier.size()==2);
		assertTrue(tier.getIntervalX(0).label==true);
		assertTrue(tier.getIntervalX(1).label==false);
	}
	
	
	@Test
	public void testSize() throws Exception {
		BinaryTier tier = new BinaryTier();
		assertTrue(tier.size()==0);

		//tier:   ---------------
		//add :     +++++++++++++
		//    :   --+++++++++++++
		tier.addInterval(new Interval<Boolean>(0.1d, 2.0d, true));
//		System.out.println(tier.debugPrint());
		assertTrue("Tier size ("+tier.size() +") is not 2",  tier.size()==2);
		assertTrue(tier.getIntervalX(0).label==false);
		assertTrue(tier.getIntervalX(1).label==true);
		
		//tier:   --+++++++++++++
		//add :        ---
		//    :   --+++---+++++++
		tier.addInterval(new Interval<Boolean>(0.4d, 0.5d, false));
		assertTrue(tier.size()==4);
		assertTrue(tier.getDuration()==2.0d);
//		System.out.println(tier.debugPrint());
		assertTrue(tier.getIntervalX(0).label==false);
		assertTrue(tier.getIntervalX(1).label==true);
		assertTrue(tier.getIntervalX(2).label==false);
		assertTrue(tier.getIntervalX(3).label==true);
		
		//tier:   --+++---+++++++
		//add :   +++++++++++++++++++++
		//    :   +++++++++++++++++++++
		tier.addInterval(new Interval<Boolean>(0.0d, 2.5d, true));
		assertTrue(tier.size()==1);
		assertTrue(tier.getDuration()==2.5d);
		assertTrue(tier.getIntervalX(0).label==true);
	}	
	
	
	@Test
	public void testGetDuration() throws Exception {
		BinaryTier tier = new BinaryTier();
		assertTrue(tier.size()==0);
		
		tier.addInterval(0.0d, 1.0d, true);
		assertTrue(tier.getDuration()==1.0d);
		assertTrue(tier.size()==1);
		assertTrue(tier.getIntervalX(0).label==true);

		tier.addInterval(1.0d, 2.0d, true);
		assertTrue(tier.getDuration()==2.0d);
		assertTrue(tier.size()==1);
		assertTrue(tier.getIntervalX(0).label==true);
		
		tier.addInterval(0.5d, 1.5d, false);
		assertTrue(tier.getDuration()==2.0d);
		
//		0.0 - 0.5 true
//		0.5 - 1.5 false
//		1.5 - 2.0 true
//		2.0 - 3.0 false
//		3.0 - 4.0 true
		tier.addInterval(3.0d, 4.0d, true);
		assertTrue(tier.getDuration()==4.0d);

		//-- add in wrong order of timestamps
		tier.addInterval(6.0d, 5.0d, true);
		assertTrue(tier.getDuration()==6.0d);

	}
	
//	@Test
	public void testAddInterval() throws Exception {
		BinaryTier tier = new BinaryTier();
		tier.addInterval(0.15f, 0.2f, true);
		tier.addInterval(0.25f, 0.8f,  true);
		tier.addInterval(0.5f, 1.5f,  true);
//		System.out.println(tier);
	}
	

}
