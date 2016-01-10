package info.pinlab.snd.fe;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;


/**
 * 
 * Reads sound files and produces frames. Default frame length is 10ms, but it can be changed.
 * 
 * <pre>{@code
 AudioFrameReader reader = new AudioFrameReader();
 reader.setFrameLenInMs(20);
 reader.setFileSource(wav);
 reader.start()
}
 * </pre>
 * 
 * @author Gabor Pinter
 */
public class FrameProvider implements AudioFrameConsumer{
	public static Logger LOG = LoggerFactory.getLogger(FrameProvider.class);
	
//	interface AudioFrameConsumer{
//		public void consume(int[] samples);
//		public void end();
//	}
	
	
	private final PipedOutputStream pos ;
	private final PipedInputStream pis ;

	private final int hz;
	private final int bytePerSample;
	private final int frameSzInByte;
	int buffSizeInMs = 5*1000;
	int buffSizeInByte = buffSizeInMs; 
//	private final ParamSheet context;

	private WavClip wav = null;
	private AudioFrameConsumer frameConsumer = null;

	private Thread writeThread = null;
	private Thread framerThread = null;



	public FrameProvider(int hz, int frameSzInMs, int bytePerSample){
		this.hz = hz;
		this.bytePerSample = bytePerSample;
		this.frameSzInByte = (int) (bytePerSample*hz*frameSzInMs/1000.0d);
		pos = new PipedOutputStream();
		pis = new PipedInputStream(hz * bytePerSample); //-- 1 second buffer
		try{
			pis.connect(pos);
		}catch(IOException ignore){};
	}
	
	
	public FrameProvider(ParamSheet context){
		this( context.get(FEParam.HZ)
			, context.get(FEParam.FRAME_LEN_MS)
			, context.get(FEParam.BYTE_PER_SAMPE)
			);
	}



	/**
	 * @param consumer to be called for every frame read
	 */
	public void setAudioFrameConsumer(AudioFrameConsumer consumer){
		frameConsumer = consumer;
	}



	@Override
	public void consume(int[] samples) {
	}
	@Override
	public void end() {
	}
	

	
	
	
	
	public FrameProvider setSource(WavClip wav){
		if(wav==null){
			throw new IllegalArgumentException("WavClip can't be null!");
		}
		AudioFormat af = wav.getAudioFormat();
		if(af.getChannels()>1){
			throw new IllegalArgumentException("WavClip can only be mono!");
		}
		if(hz != (int)af.getSampleRate()){
			throw new IllegalArgumentException("WavClip has wrong sampling rate("+ af.getSampleRate() +"vs " + hz + ")");
		}
		this.wav=wav;
		return this;
	}


	private class AudioByteReader implements Runnable{
		@Override
		public void run() {
			try{
				while(writeThread!=null && !writeThread.isInterrupted()){
					if(wav !=null){ //-- read from wav!
						for(byte b : wav.getSamples()){
//						System.out.println(b);
							pos.write(b);
//							try{
////								System.out.println(Thread.currentThread());
//								Thread.currentThread().sleep(10);
//							}catch(InterruptedException e){};
						}
					}
					//-- else
					break;
				}
				pos.flush();
				pos.close();
			} catch(IOException e){		}
		}
	}
	

	
	/**
	 * Starts reading audio and creating frames 
	 */
	public void start(){
		writeThread = new Thread(new AudioByteReader(), "Sample Byte Reader");
		writeThread.start();

		framerThread = new Thread(new Framer(frameSzInByte), "Sample Consumer");
		framerThread.start();
	}

	private class Framer implements Runnable{
		//		private byte [] buff;
		private final int frameLenInByte;
		private final int frameLenInSample;
		private final int frameHalfLenInSample;
		
		
		int [] prevFrame ; 
		
		int frameN = 0;
		
		Framer(int frameLenInByte){
			this.frameLenInByte = frameLenInByte;
			frameLenInSample = frameLenInByte/bytePerSample;
			frameHalfLenInSample = frameLenInSample / 2;
		}

		int [] getIntsFrom16bitLe(byte [] buff){
			int [] samples = new int[frameLenInSample];
			for(int i = 0; i < samples.length ; i++){
				samples[i] = buff[(i*bytePerSample+1)];
				samples[i] = samples[i] << 8;
				samples[i] |=  buff[(i*bytePerSample+0)];
			}
			return samples;
		}
		
		@Override
		public void run(){
			int off = 0;
			int len = frameLenInByte;
			byte [] buff = new byte[len];
			int sz = 1;
			prevFrame = new int[frameLenInSample];
			
			try {
				//-- first round:
				while((sz = pis.read(buff, off, len))>=0){
					off += sz;
					len -= sz;
					if(off==frameLenInByte){ //-- full!
						if(frameConsumer != null){
							final int [] frame = getIntsFrom16bitLe(buff);
							frameConsumer.consume(frame);
							frameN++;
//							System.out.println("Framer of " + frameN);
							//-- copy 2nd half of samples into prevFrame
							prevFrame = new int[frameLenInSample];
							System.arraycopy(frame, frameHalfLenInSample, prevFrame, 0, frameHalfLenInSample);
						}
						off = 0;
						len = frameLenInByte;
						buff = new byte[len];
						break;
					}
				}
				//-- second round onwards:
				while((sz = pis.read(buff, off, len))>=0){
					off += sz;
					len -= sz;
//					System.out.println(" buff sz " + sz + " " + off + " " + len);
					if(off==frameLenInByte){ //-- full!
//						System.out.println("FULL! sample sz ");
						if(frameConsumer != null){
							//-- convert byte [] to samples...
							final int [] frame = getIntsFrom16bitLe(buff);
							System.arraycopy(frame, 0,      
									         prevFrame, frameHalfLenInSample, frameHalfLenInSample);
							frameConsumer.consume(prevFrame);
							frameConsumer.consume(frame);
							
							System.arraycopy(frame, frameHalfLenInSample, 
											prevFrame, 0, frameHalfLenInSample);
							frameN++;
							
							//-- for TESTING/DEBUGGING 
//							System.out.println("Framer of " + frameN);
//							try {
//								Thread.sleep(100); //-- slow it down
//							} catch (InterruptedException ignore){	}
						}
						off = 0;
						len = frameLenInByte;
						buff = new byte[len];
					}
				}
				pis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LOG.info("Stream end reached");
			if(frameConsumer != null){
				frameConsumer.end();
			}
		}
	}


	/**
	 * 
	 * @return size of the frame in samples
	 */
	public int getFrameSizeInSample(){
		return this.frameSzInByte/this.bytePerSample;
	}


	public void close(){
		if(writeThread!=null){
			writeThread.interrupt();
			writeThread=null;
		}
		try{
			pos.close();
		}catch(IOException e){}
	}



	public static void main(String[] args) throws Exception{
		InputStream is = FrameProvider.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		int [] s = wav.toIntArray();
		for(int i = 0; i < 16 ; i++){
			System.out.println(s[i]);
		}
		System.out.println("***************");
		
		ParamSheet context = new ParamSheet.ParamSheetBuilder().setFrameLenInMs(1).build();
		
		FrameProvider pipe = new FrameProvider(context);
//		AudioFrameReader pipe = new AudioFrameReader(1, wav.getAudioFormat());
		pipe.setSource(wav);
		pipe.setAudioFrameConsumer(new AudioFrameConsumer() {
			@Override
			public void consume(int[] samples) {
//				System.out.println(" size: " + samples.length);
				for(int i = 0; i < samples.length ; i++){
					
					System.out.print(samples[i]+" ");
//					if((i+1)==samples.length/2){
//						System.out.print(" |  ");
//					}
				}
				System.out.println("");
//				System.exit(0);
			}

			@Override
			public void end() {
				System.out.println("DONE!");
			}
		});
		pipe.start();

	}


}








