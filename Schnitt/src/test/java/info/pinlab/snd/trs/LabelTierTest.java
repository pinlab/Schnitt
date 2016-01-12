package info.pinlab.snd.trs;

import static org.junit.Assert.assertTrue;

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
		tier.addInterval(0.0d, 1.0d, "a");
		assertTrue(tier.size()==1);
		tier.addInterval(1.0d, 2.0d, "b");
		assertTrue(tier.size()==2);
		
		//-- add with a gap: non-defined interval
		tier.addInterval(4.0d, 5.0d, "c");
		assertTrue(tier.size()==4);
		
//		for(Interval<String> inte : tier){
//			System.out.print(inte);
//		}
//		System.out.println("|");
//		System.out.println(tier);
	}
	
	@Test
	public void testWhenAddingBridginInterval() {
//   a  |aaaaaaaaaaaaaaaaaaaaaa|
//   b         |bbbbbbb|
// a+b  |aaaaaa|bbbbbbb|       |
//		
		LabelTier tier = new LabelTier();
		assertTrue(tier.size()==0);
		tier.addInterval(0.0d, 1.0d, "a");
		assertTrue(tier.size()==1);
		tier.addInterval(0.4d, 0.6d, "b");
		assertTrue(tier.size()==3);
		//-- the last label should be "" 
		assertTrue(tier.get(0.6d).equals(""));
//		System.out.println(tier.toStringDebug());
//		System.out.println(tier);
	}
	
	@Test
	public void testWhenMaskingInterval() {
		LabelTier tier = new LabelTier();
		assertTrue(tier.size()==0);
		tier.addInterval(0.0d, 1.0d, "a");
		tier.addInterval(1.0d, 2.0d, "b");
		tier.addInterval(2.0d, 3.0d, "c");
		assertTrue(tier.size()==3);

	//abc   |aaaaaa|bbbbbbb|ccccccc|
	//   x       |xxxxxxxxxxxx|
	// a+b  |aaaa|xxxxxxxxxxxx|    |
		
		tier.addInterval(0.5d, 2.5d, "x");
//		System.out.println(tier.toStringDebug());
	}

}
