package info.pinlab.snd.vad;

import static org.junit.Assert.*;
import org.junit.Test;

import info.pinlab.snd.fe.DoubleFrame;
import info.pinlab.snd.fe.FrameAlignment;
import info.pinlab.snd.fe.ParamSheet;
import info.pinlab.snd.trs.BinaryTier;
import info.pinlab.snd.trs.DoubleFrameTier;

public class ThresholdVadTest {

	@Test
	public void testSetParam() throws Exception {
		VAD vad = new ThresholdVad()
			.setParam(ThresholdVad.THRESH, 0.3)
			.setParam(ThresholdVad.THRESH_FILTER_LOW, false)
			.setParam(ThresholdVad.THRESH_TARG, "hahota")
			;

		assertTrue(vad.getParam(ThresholdVad.THRESH) == 0.3);
		assertTrue(vad.getParam(ThresholdVad.THRESH_TARG) == "hahota");
		assertTrue(vad.getParam(ThresholdVad.THRESH_FILTER_LOW) == false);
	}
	@Test
	public void testSetParamWithParamSheet() throws Exception {
		
		ParamSheet sheet =	new ParamSheet.ParamSheetBuilder().build();

	}
	
	
	
	@Test
	public void testSetFilter() throws Exception {
		double t0 = 0.08;
		BinaryTier target = new BinaryTier();
		target.addInterval(t0, 0.12, true);
		target.addInterval(0.18, 0.28, true);
		target.addInterval(0.61, 0.62, true);
		target.addInterval(0.64, 0.65, true);

		double hz = 16000;
		int winLenMs = 100;
		int winLenSample = (int) (hz*winLenMs)/1000  ;
		double winShiftSample = (16000*winLenMs/1000)/2;
		DoubleFrameTier tier = new DoubleFrameTier((int)hz, winLenSample);
		//Overlap: 0.2,0.4,0.4,0.7,1.0 
		int frameIx = 0;
		double t = frameIx/hz;
		while(t <= 1.0){
//			System.out.println(frameIx + " " + winShiftSample);
			DoubleFrame frame = new DoubleFrame(new double[0], "empty", frameIx);
			tier.add(frame);
			frameIx += winShiftSample;
			t = frameIx/hz;
		}
		
		FrameAlignment sorter = new FrameAlignment(tier, target);
		sorter.calcOverlap();
		
		System.out.println(target.debugPrint());

		VAD vad = new ThresholdVad();
		vad	.setFrameTier(tier)
			.setParam(ThresholdVad.THRESH, 0.3)
			.setParam(ThresholdVad.THRESH_TARG, "overlap")
			;
		
		
		
		BinaryHypoTier hypo = vad.getHypo();
		System.out.println("END");
		System.out.println(hypo.debugPrint());

		assertTrue(hypo != null);
		assertTrue(hypo.getIntervalX(0).endT < t0);
		
		
	}
}
