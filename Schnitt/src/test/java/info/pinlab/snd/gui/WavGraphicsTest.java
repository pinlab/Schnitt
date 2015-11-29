package info.pinlab.snd.gui;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WavGraphicsTest {
	int n = 1000;
	int hz = 16000;
	int [] samples = new int[n];

	WavPanelModel model ;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception{
		for(int i=0; i<n; i++){
			samples[i] = i;
		}
		model = new WavGraphics();
		model.setSampleArray(samples, hz);
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testSetPositionInMs(){
		for(int i=0; i<n; i++){
			int ms = i;
			model.setCursorPosToMs(ms);
			int ms2  = model.getCursorPositionInMs();
			assertTrue(ms==ms2);
		}
	}
	
	
	@Test
	public void testSetPositionInPx(){
		int w = 999;
		for(int j=0; j < 10 ; j++){
			model.setViewWidthInPx(w + j);
		
			for(int i=0; i<n; i++){
				int pos = i;
				model.setCursorPosToPx(pos);
				int pos2 = model.getCursorPositionInPx();
//				System.out.println(pos + " = " + pos2 +" (w=" + (w+j) +")\n");
				assertTrue(pos==pos2);
			}
		}
	}
	
}
