package info.pinlab.snd.trs;
import static org.junit.Assert.*;

import org.junit.Test;


public class BinaryTierTest {

	
	@Test
	public void testGetDuration() throws Exception {
		BinaryTier tier = new BinaryTier();
		assertTrue(tier.size()==0);
		
		tier.addInterval(0.0d, 1.0d, true);
		assertTrue(tier.getDuration()==1.0d);
		
//		tier.addInterval(1.5d, 0.5d, false);
		tier.addInterval(0.5d, 1.5d, false);
		System.out.println(tier.getDuration());
		
		assertTrue(tier.getDuration()==1.5d);
		
		
		
		
	}
	
	@Test
	public void testAddInterval() throws Exception {
//		BinaryTier tier = new BinaryTier();
//		tier.addInterval(0.15f, 0.2f, true);
//		tier.addInterval(0.25f, 0.8f,  true);
//		tier.addInterval(0.5f, 1.5f,  true);

		
//		throw new RuntimeException("not yet implemented");
	}

}
