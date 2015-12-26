package info.pinlab.snd.dsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;

import info.pinlab.pinsound.WavClip;


/**
 * 
 * Reads sound files and produces frames. Default frame length is 10ms, but it can be changed.
 * 
 * <pre>
 * {@code
 * 
AudioFrameReader pipe = new AudioFrameReader();
pipe.setFrameLenInMs(1);
pipe.setFileSource(wav);
pipe.start()
 * 
 * }
 * </pre>
 * 
 * @author Gabor Pinter
 */
public class AudioFrameReader{

	private final PipedOutputStream pos ;
	private final PipedInputStream pis ;

	
	AcousticContext context;
	
	int hz = 16000;
//	int bytePerSample = 2;
	int buffSizeInMs = 5*1000;
	int buffSizeInByte = buffSizeInMs; 

//	private int frameLenInMs = 10; //
//	int frameLenInByte = (frameLenInMs*hz*2)/1000;   /* */; //-- 10ms x 16kHz x depth (16bit -> 2byte)
//	int frameShiftLenInByte = frameLenInByte/2; 


	private WavClip wav = null;
	private AudioFrameConsumer frameConsumer = null;


	interface AudioFrameConsumer{
		public void consume(int[] samples);
	}


	public AudioFrameReader(int frameLenInMs){
		this(new AcousticContext.AcousticContextBuilder().setFrameLenInMs(frameLenInMs).build());
	}
	
	public AudioFrameReader(){
		this(new AcousticContext.AcousticContextBuilder().build());
	}

	public AudioFrameReader(AcousticContext context){
//		if(af!=null){
//			if(af.getChannels()>1){
//				throw new IllegalArgumentException("Only mono sound is allowed");
//			}
//			hz = (int)af.getSampleRate();
//			bytePerSample = af.getSampleSizeInBits()/8;
//		}
		
		this.context = context;
//		this.frameLenInMs = frameLenInMs;
//		frameLenInByte = (this.frameLenInMs*hz*2)/1000;

		
		pos = new PipedOutputStream();
		pis = new PipedInputStream(context.hz*context.bytePerSample); //-- 1 second buffer
		try{
			pis.connect(pos);
		}catch(IOException ignore){};
	}



	/**
	 * 
	 * @param consumer to be called for every frame read
	 */
	public void setAudioFrameConsumer(AudioFrameConsumer consumer){
		frameConsumer = consumer;
	}


	public void setFileSource(WavClip wav){
		if(wav==null){
			throw new IllegalArgumentException("WavClip can't be null!");
		}
		AudioFormat af = wav.getAudioFormat();
		if(af.getChannels()>1){
			throw new IllegalArgumentException("WavClip can only be mono!");
		}
		if(context.hz != (int)af.getSampleRate()){
			throw new IllegalArgumentException("WavClip has wrong sampling rate("+ af.getSampleRate() +"vs " + context.hz + ")");
		}
		
//		hz = (int)af.getSampleRate();
//		bytePerSample = af.getSampleSizeInBits()/8;
//		frameLenInByte = (frameLenInMs*hz*bytePerSample)/1000;   /* */; //-- 10ms x 16kHz x depth (16bit -> 2byte)
		
		this.wav=wav;
	}


//	public void setFrameLenInMs(int ms){
//		
//		
//		frameLenInMs = ms;
//		frameLenInByte = (frameLenInMs*hz*bytePerSample)/1000;   /* */; //-- 10ms x 16kHz x depth (16bit -> 2byte)
//	}


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
//								Thread.sleep(10);
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
	

	private Thread writeThread = null;
	private Thread framerThread = null;

	
	/**
	 * Starts reading audio and creating frames 
	 */
	public void start(){
		writeThread = new Thread(new AudioByteReader(), "Sample Byte Reader");
		writeThread.start();

		framerThread = new Thread(new Framer(context.frameLenInByte), "Sample Consumer");
		framerThread.start();
	}

	private class Framer implements Runnable{
		//		private byte [] buff;
		private final int frameLenInByte;
		private final int frameLenInSample;
		private final int frameHalfLenInSample;
		
		int [] prevFrame ; 
		
		Framer(int frameLenInByte){
			this.frameLenInByte = frameLenInByte;
			frameLenInSample = frameLenInByte/context.bytePerSample;
			frameHalfLenInSample = frameLenInSample / 2;
		}

		int [] getIntsFrom16bitLe(byte [] buff){
			int [] samples = new int[frameLenInSample];
			for(int i = 0; i < samples.length ; i++){
				samples[i] = buff[(i*context.bytePerSample+1)];
				samples[i] = samples[i] << 8;
				samples[i] |=  buff[(i*context.bytePerSample+0)];
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
			
			
			
		}
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
		InputStream is = AudioFrameReader.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		int [] s = wav.toIntArray();
		for(int i = 0; i < 16 ; i++){
			System.out.println(s[i]);
		}
		System.out.println("***************");
		
		AcousticContext context = new AcousticContext.AcousticContextBuilder().setFrameLenInMs(1).build();
		
		AudioFrameReader pipe = new AudioFrameReader(context);
//		AudioFrameReader pipe = new AudioFrameReader(1, wav.getAudioFormat());
		pipe.setFileSource(wav);
		pipe.setAudioFrameConsumer(new AudioFrameConsumer() {
			@Override
			public void consume(int[] samples) {
				System.out.println(" size: " + samples.length);
				for(int i = 0; i < samples.length ; i++){
//					System.out.println("\t[" + (samples[0]*(samples.length-1)+i-1) + "]  " + samples[i]);
					System.out.print(samples[i]+" ");
//					if((i+1)==samples.length/2){
//						System.out.print(" |  ");
//					}
				}
				System.exit(0);
			}
		});
		pipe.start();

	}
}








