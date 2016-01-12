package info.pinlab.snd.fe;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;
import info.pinlab.snd.trs.DoubleFrameTier;

public class StreamingAcousticFrontEndTest {
	public static Logger LOG = LoggerFactory.getLogger(StreamingAcousticFrontEndTest.class);
	
//	@Test
	public void testSetWav() throws Exception {
		WavClip wav = new WavClip(StreamingAcousticFrontEndTest.class.getResourceAsStream("sample.wav"));
		assertTrue(wav!=null);

		OUTER: for(int frameInMs : new int[]{10, 20, 30, 50, 100, 500}){
			for(int shiftInMs = 1 ; shiftInMs <= frameInMs ; shiftInMs++){
				if (shiftInMs > frameInMs){ //- shift should be shorter
					continue OUTER;
				}
				if (frameInMs % shiftInMs != 0){ //-- not multi
					continue;
				}
				LOG.trace("Frame len:" + frameInMs + "ms, shift " + shiftInMs +"ms");
				
				//				System.out.println(frameInMs + "\t" + shiftInMs);
				ParamSheet params = new ParamSheetBuilder()
						.set(FEParam.HZ, 16000)
						.set(FEParam.FRAME_LEN_MS, frameInMs)
						.set(FEParam.FRAME_SHIFT_MS, shiftInMs)
						.build();
				StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(params);
				fe.setWav(wav);
				fe.start();
				DoubleFrameTier tier = fe.getFrameTier();

				Double prev = null;
				for(Double t : tier.getTimeLabels()){
					//					System.out.println(t);
					if(prev==null){
						prev = t;
						continue;
					}
					int diff = (int)Math.round( Math.abs(t-prev)*1000);
					assertTrue("Shift is " + diff + "ms instead of " + shiftInMs + "ms!", diff==shiftInMs);

					DoubleFrame frame = tier.getFrameAt(t);
					assertTrue(frame!=null);
					double startT = frame.getStartSampleIx()/16000.0d;
					double diffOfT = Math.abs(t-startT);
					assertTrue("Start times mismatch: " + startT + " != " + t + "  (diff: " + (diffOfT) + ")", diffOfT  < 0.00001);
					prev = t;
				}
			}

		}
	}

//	@Test
	public void testOverlap() throws Exception {
		WavClip wav = new WavClip(StreamingAcousticFrontEndTest.class.getResourceAsStream("sample.wav"));
		assertTrue(wav!=null);

		
		int HZ = 16000;
		OUTER: for(int frameInMs : new int[]{1, 8, 10, 20, 30, 50, 100, 500}){
			for(int shiftInMs = 1 ; shiftInMs <= frameInMs ; shiftInMs++){
				if (shiftInMs > frameInMs){ //- shift should be shorter
					continue OUTER;
				}
				if (frameInMs % shiftInMs != 0){ //-- not multi
					continue;
				}
				LOG.trace("Frame len:" + frameInMs + "ms, shift " + shiftInMs +"ms");

				int frameShiftInSample = (HZ/1000)*shiftInMs  ;

				//-- calculate features frame timestamps (no actual processing
				ParamSheet params = new ParamSheetBuilder()
						.set(FEParam.HZ, HZ)
						.set(FEParam.FRAME_LEN_MS, frameInMs)
						.set(FEParam.FRAME_SHIFT_MS, shiftInMs)
						.build();
				StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(params);
				fe.setWav(wav);
				fe.start();
				DoubleFrameTier tier = fe.getFrameTier();

				DoubleFrame prevFrame = null;
				Double prevT = null;
				for(Double t : tier.getTimeLabels()){
					DoubleFrame frame = tier.getFrameAt(t);
					if(prevFrame!=null){
						int diff = (int)Math.round((t-prevT)*1000);
						assertTrue("Shift is " + diff + "ms instead of " + shiftInMs + "ms!", diff==shiftInMs);
		//				System.out.println(t);
		//				System.out.println(Arrays.toString(frame.getArray("sample")));
		//				System.out.println(Arrays.toString(prevFrame.getArray("sample")));
						double [] frameArr = frame.getArray("sample");
						double [] prevFrameArr = prevFrame.getArray("sample");
						for(int i = 0; i < frameArr.length ; i++){
							int prevIx = frameShiftInSample+i;
							if(prevIx < prevFrameArr.length){
//								System.out.println("[" + i +"] " + prevFrameArr[prevIx] +"\t" + frameArr[i]);
								assertTrue(frameArr[i] == prevFrameArr[prevIx]);

//							}else{
//								System.out.println("[" + i +"] " +  "\t" +"\t" + frameArr[i]);
							}
						}
						break;
					}
					prevT = t;
					prevFrame = frame;

				}
			}
		}
	}

}
