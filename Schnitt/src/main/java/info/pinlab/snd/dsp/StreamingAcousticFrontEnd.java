package info.pinlab.snd.dsp;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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
	private final AudioFrameReader frameReader; 

	Thread processorThread;


	private final FrameProcessor [] pipeline;


	public StreamingAcousticFrontEnd(AcousticContext context){
		this.context = context;

		frameReader = new AudioFrameReader(context);
		frameReader.setAudioFrameConsumer(this);

		sampleMax = Math.pow(2, (context.bytePerSample*8));
		inDeque = new ConcurrentLinkedDeque<DoubleFrame>();
		outDeque = new ConcurrentLinkedDeque<DoubleFrame>();

		pipeline = new FrameProcessor[1];
		try {
			//-- INIT frame processors here!

			HanningWindower hannWindower = FrameProcessorFactory.create(HanningWindower.class, context);
			MelFilter melFilter = FrameProcessorFactory.create(MelFilter.class, context);

			pipeline[0] = hannWindower;


		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void consume(int[] samples){
		double [] arr = new double [samples.length]; 
		for(int i = 0; i < samples.length; i++){
			arr[i] = samples[i]/sampleMax;
		}
		int id = frameN*(context.frameLenInSample/2);
		DoubleFrame frame = new DoubleFrame(arr, "sample", id);
		frameN++;		
		synchronized (inDeque) {
			inDeque.add(frame);
			inDeque.notify();
		}

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
			LOOP: while(true){
				DoubleFrame frame = inDeque.pollFirst();
				if(frame==null){
					synchronized (inDeque) {
						try {
							inDeque.wait();
						} catch (InterruptedException ignore) {
							ignore.printStackTrace();
						}
					}
					continue LOOP;
				}
				System.out.println(frameN++);

				//-- do the pipeline
				for(FrameProcessor processor : pipeline){
					processor.process(frame);
				}

				//-- at the end:
				outDeque.addFirst(frame);
				System.out.println(Arrays.toString(frame.get("sample")));
			}
		}
	}


	public void setWav(WavClip wav){
		processorThread = new Thread(new FeatureProcessor());

		frameReader.setFileSource(wav);
	}


	public void start(){
		processorThread.start();
		frameReader.start();
	}


	public static void main(String[] args) throws Exception{
		InputStream is = StreamingAcousticFrontEnd.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		AcousticContext context = new AcousticContextBuilder().setFrameLenInMs(10).build();
		System.out.println(context.frameLenInSample);

		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);

		fe.setWav(wav);
		fe.start();

	}

}
