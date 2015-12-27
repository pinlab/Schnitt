package info.pinlab.snd.dsp;

import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedDeque;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.dsp.AcousticContext.AcousticContextBuilder;
import info.pinlab.snd.dsp.AudioFrameReader.AudioFrameConsumer;

/**
 * 
 * Calculates acoustic features in streams. 
 * In future: parallel, native support + GPU accelerated
 * 
 * @author Gabor Pinter
 *
 */
public class StreamingAcousticFrontEnd implements AudioFrameConsumer{

	private final ConcurrentLinkedDeque<DoubleFrame> inDeque;
	private final ConcurrentLinkedDeque<DoubleFrame> outDeque;


	int frameArrSize;
	private final AcousticContext context;
	private final double sampleMax;
	private int frameN = 0;
	private MelFilterBank melFilter;

	private final AudioFrameReader frameReader; 

	Thread processorThread;


	public StreamingAcousticFrontEnd(AcousticContext context){
		this.context = context;

		frameReader = new AudioFrameReader(context);
		frameReader.setAudioFrameConsumer(this);

		sampleMax = Math.pow(2, (context.bytePerSample*8));
		inDeque = new ConcurrentLinkedDeque<DoubleFrame>();
		outDeque = new ConcurrentLinkedDeque<DoubleFrame>();

		melFilter = new MelFilterBank(context);


		processorThread = new Thread(new FeatureProcessor());

	}

	@Override
	public void consume(int[] samples){
		double [] arr = new double [samples.length]; 
		for(int i = 0; i < samples.length; i++){
			arr[i] = samples[i]/sampleMax;
		}
		int id = frameN*(context.frameLenInSample/2);
		DoubleFrame frame = new DoubleFrame(arr, id);
		frameN++;		
		inDeque.add(frame);
	}

	@Override
	public void end(){
		System.out.println("END!");

//		processorThread.interrupt();
//		synchronized (this) {
//			notify();
//		}
	}

	private class FeatureProcessor implements Runnable{

		@Override
		public void run(){
			int frameN = 0;
			
//			System.out.println("RUN");
//			synchronized (processorThread){
				LOOP: while(true){
//					System.out.println("  loop ");
					DoubleFrame frame = inDeque.pollFirst();
					if(frame==null){
//						System.out.println("  null frame");
						try {
							Thread.sleep(20);
//							processorThread.wait();
							continue LOOP;
						} catch (InterruptedException ignore){	}
					}
					System.out.println(frameN++);
					double [] mfc = melFilter.process(frame.getSamples());
					frame.setFeatrues(mfc);

					//-- at the end:
					outDeque.addFirst(frame);
				}
//			}
		}
	}


	public void setWav(WavClip wav){
		frameReader.setFileSource(wav);
	}


	public void start(){
		processorThread.start();
		frameReader.start();
	}s


	public static void main(String[] args) throws Exception{
		InputStream is = StreamingAcousticFrontEnd.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		AcousticContext context = new AcousticContextBuilder().setFrameLenInMs(1).build();
		System.out.println(context.frameLenInSample);
		
		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);

		fe.setWav(wav);
		fe.start();
		
		Thread.sleep(5000);
		System.out.println("DONE!");
	}

}
