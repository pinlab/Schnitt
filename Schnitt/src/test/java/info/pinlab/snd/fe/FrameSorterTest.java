package info.pinlab.snd.fe;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.DoubleFrameTier;

public class FrameSorterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	
	
	@Test
	public void test(){
		BinaryTier target = new BinaryTier();
		target.addInterval(0.08, 0.12, true);
		target.addInterval(0.18, 0.60, true);
		target.addInterval(0.61, 0.62, true);
		target.addInterval(0.64, 0.65, true);
		
		double hz = 16000;
		int winLenMs = 100;
		int winLenSample = (int) (hz*winLenMs)/1000  ;
		double winShiftSample = (16000*winLenMs/1000)/2;
		//Overlap: 0.2,0.4,0.4,0.7,1.0 
		
		DoubleFrameTier frames = new DoubleFrameTier((int)hz, winLenSample);
		
		
		
		int frameIx = 0;
		double t = frameIx/hz;
		while(t <= 1.0){
			System.out.println(frameIx + " " + winShiftSample);
			DoubleFrame frame = new DoubleFrame(new double[0], "empty", frameIx);
			frames.add(frame);
			frameIx += winShiftSample;
			t = frameIx/hz;
		}
		
		assertTrue(frames.size() > 0);

		FrameSorter sorter = new FrameSorter(frames, target);
		System.out.println("START SORTING");
		sorter.sort();
		//TODO: check overlaps!
		
		System.out.println(sorter);
	}
	
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

}
