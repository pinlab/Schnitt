package info.pinlab.snd.trs;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.snd.fe.DoubleFrame;

public class DoubleFrameTierTest {

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
	public void testTimeStamps() throws Exception {
		double hz = 16000;
		int winLenMs = 10;
		int winLenSample = (int)hz*winLenMs/1000;
		double winShiftSample = winLenSample/2;
		double frameLenInMs = (winLenSample / (double)hz)*1000;
		
		
		DoubleFrameTier tier = new DoubleFrameTier((int)hz, winLenMs);
		System.out.println(winLenSample + " == " + tier.getFrameLenInSec() + "(" + frameLenInMs +")");
		assertTrue(tier.getFrameLenInSec() -  winLenMs*1000.0d < 0.00001);
		
		int frameIx = 0;
		double at = frameIx/hz;
		while(at <= 1.0){
//			System.out.println(frameIx + " " + at);
			DoubleFrame frame = new DoubleFrame(new double[0], "empty", frameIx);
			tier.add(frame);
			frameIx += winShiftSample;
			at = frameIx/hz;
		}
		
	}

	@Test
	public void testTimeStampsWithAudio() throws Exception {
		//TODO: load wav
		//      do only framing
		//      check timestamps
		
//		assertTrue(tier.size()>0);
//		Double prev = -10.0d; 
//		for(Double t : tier.getTimeLabels()){
//			assertTrue(prev<t);
//			double shift = t-prev;
//			double diff = shift - winShiftSample;
//			System.out.println(shift + "\t "+ winShiftSample +"\t\t"+ prev +"\t" + t);
//			assertTrue( Math.abs(diff) < 0.0000001 /* precision */) ;
//			prev = t;
//		}
	}
	
}
