package info.pinlab.snd.trs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LabelTierTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void testSize() {
		LabelTier tier = new LabelTier();
		assertTrue(tier.size()==0);
//		tier.addInterval(0.0d, 1.0d, "a");
//		assertTrue(tier.size()==1);
//		tier.addInterval(1.0d, 2.0d, "b");
//		assertTrue(tier.size()==2);
//		
//		//-- jumping a bit
//		tier.addInterval(4.0d, 5.0d, "c");
//		assertTrue(tier.size()==4);
////		tier.debugPrint();
	}
	
	@Test
	public void testSizeWhenAddingBridginInterval() {
		LabelTier tier = new LabelTier();
		assertTrue(tier.size()==0);
		tier.addInterval(0.0d, 1.0d, "a");
		tier.addInterval(0.4d, 0.6d, "b");
//   a  |aaaaaaaaaaaaaaaaaaaaaa|
//   b         |bbbbbbb|
// a+b  |aaaaaa|bbbbbbb|       |
		
		tier.debugPrint();
	}
	
	
	@Test
	public void test() {
		LabelTier tier = new LabelTier();
//		tier.addInterval(0.0f, 1.0f, "start");
		tier.addInterval(0.0d, 0.5d, "first");
		tier.addInterval(0.0d, 0.8d, "second");

		
//		tier.debugPrint();
	}

}
