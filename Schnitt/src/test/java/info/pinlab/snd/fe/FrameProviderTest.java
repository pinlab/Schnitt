package info.pinlab.snd.fe;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.fe.FrameProvider.AudioFrameConsumer;

public class FrameProviderTest {

	List<DoubleFrame> frames = new ArrayList<DoubleFrame>();

	static class Consumer implements AudioFrameConsumer{
		public int frameLen = -1;
		public int frameLenOld = -1;
		public int frameN = -1;
		
		@Override
		public void consume(int[] samples) {
			frameN++;
			frameLen = samples.length;
			if(frameN==0){
				frameLenOld = samples.length;
			}
//			System.out.println(frameLen +" " + frameLenOld);
			assertTrue(frameLen==frameLenOld);
			frameLenOld = frameLen;
		}
		
		@Override
		public void end(){
			synchronized (this) {
				notify();
			}
		}
		
		public int getFrameN(){	return this.frameN;	   }
		public int getFrameLen(){return this.frameLen; }
	}; 
	
	
	
	@Test
	public void testConstructorParams() throws Exception {
		FrameProvider reader ; 
		reader = new FrameProvider(16000, 10, 2);
		assertTrue(reader.getFrameSizeInSample() == 160);

		reader = new FrameProvider(16000, 20, 2);
		assertTrue(reader.getFrameSizeInSample() == 320);

		reader = new FrameProvider(16000, 10, 1);
		assertTrue(reader.getFrameSizeInSample() == 160);

		reader = new FrameProvider(16000, 20, 1);
		assertTrue(reader.getFrameSizeInSample() == 320);

		reader = new FrameProvider(8000, 10, 1);
		assertTrue(reader.getFrameSizeInSample() == 80);

		reader = new FrameProvider(8000, 20, 1);
		assertTrue(reader.getFrameSizeInSample() == 160);
	}

	@Test
	public void testSetSource() throws Exception {

		WavClip wav = new WavClip(FrameProviderTest.class.getResourceAsStream("sample.wav"));
		assertTrue(wav!=null);

		FrameProvider reader = new FrameProvider(16000, 10, 2);
		assertTrue(reader.getFrameSizeInSample() == 160);
		
		Consumer consumer = new Consumer();
		reader.setSource(wav);
		reader.setAudioFrameConsumer(consumer);
		synchronized (consumer) {
			reader.start();
			consumer.wait(); // wait till it finishes
		}
		assertTrue(consumer.getFrameN()>0);
		assertTrue(consumer.getFrameLen()==160);
	}

}
