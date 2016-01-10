package info.pinlab.snd.fe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FftTest {

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
	public void testProcess() throws Exception{
		double [] hann = HanningWindower.HANN_320;
		Fft fft = new Fft(128, hann.length);
		double [] amp = fft.process(hann);		
//		for(double d: amp){
//			System.out.println(d);
//		}
	}

}
