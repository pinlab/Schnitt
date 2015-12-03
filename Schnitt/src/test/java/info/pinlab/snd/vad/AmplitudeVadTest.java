package info.pinlab.snd.vad;

import static org.junit.Assert.*;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.pinsound.WavClip;

public class AmplitudeVadTest {

	static WavClip wav = null;
	
	@BeforeClass
	public static void tearDownAfterClass() throws Exception {
		InputStream is = AmplitudeVadTest.class.getResourceAsStream("sample.wav");
		assertTrue(is!=null);
		wav = new WavClip(is);
	}

	@Before
	public void setUp() throws Exception {
	}
	
	
	
	
	@Test
	public void testVadInit(){
		new AmplitudeVad();
		
		
		
		
	}
}
