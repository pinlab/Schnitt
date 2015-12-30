package info.pinlab.snd.dsp;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedDeque;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.dsp.FrameProducer.AudioFrameConsumer;
import info.pinlab.snd.dsp.ParameterSheet.BaseParams;
import info.pinlab.snd.dsp.ParameterSheet.ParameterSheetBuilder;

/**
 * 
 * Calculates acoustic features in streams. 
 * In future: parallel, native support + GPU accelerated
 * 
 * @author Gabor Pinter
 *
 */
public class StreamingAcousticFrontEnd implements AudioFrameConsumer{

	private final ConcurrentLinkedDeque<Frame> inDeque;
	private final ConcurrentLinkedDeque<DoubleFrame> outDeque;


	int frameArrSize;
	private final ParameterSheet context;
	private final double sampleMax;
	private int frameN = 0;
	private final FrameProducer frameReader; 

	Thread processorThread;

	private final FrameProcessor [] pipeline;


	private final int frameShiftInSample; 
	
	public StreamingAcousticFrontEnd(ParameterSheet context){
		this.context = context;

		frameShiftInSample = context.getInt(BaseParams.FRAME_LEN_SAMPLE)/2;
		
		frameReader = new FrameProducer(this.context);
		frameReader.setAudioFrameConsumer(this);
		
		sampleMax = (Math.pow(2, (this.context.getInt(BaseParams.BYTE_PER_SAMPE)*8)))/2;
		inDeque = new ConcurrentLinkedDeque<Frame>();
		outDeque = new ConcurrentLinkedDeque<DoubleFrame>();

		pipeline = new FrameProcessor[1];
		try {
			//-- INIT frame processors here!

			HanningWindower hannWindower = FrameProcessorFactory.create(HanningWindower.class, this.context);
			MelFilter melFilter = FrameProcessorFactory.create(MelFilter.class, this.context);

			FrameProcessorFactory.create(Fft.class, this.context);
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
		int id = frameN*frameShiftInSample;
		DoubleFrame frame = new DoubleFrame(arr, "sample", id);
		frameN++;		
		synchronized (inDeque) {
			inDeque.add(frame);
			inDeque.notify();
		}
	}

	@Override
	public void end(){
		synchronized (inDeque) {
			inDeque.add(new EndFrame());
//					new DoubleFrame(new double[]{0.0}, "end", -1));
			inDeque.notify();
			System.out.println("END!");
		}
	}


	private class FeatureProcessor implements Runnable{
		
		@Override
		public void run(){
			LOOP: while(true){
				Frame frame = inDeque.pollFirst();
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
				
				if(frame instanceof DoubleFrame){
					System.out.println(frame.getStartSampleIx());
					//-- do the pipeline
					DoubleFrame dblFrame = (DoubleFrame)frame;
					for(FrameProcessor processor : pipeline){
						processor.processWrapper(dblFrame);
					}
					
					//-- at the end:
					outDeque.addFirst(dblFrame);
					continue LOOP;
				}else
				if(frame instanceof EndFrame){
					break LOOP;
				}
				//-- any other frames? are possible?
				
				//-- not double frame
				break LOOP;
//				System.out.println(Arrays.toString(frame.get("sample")));
			}
		}
	}


	public void setWav(WavClip wav){
		processorThread = new Thread(new FeatureProcessor(), "feature-processor");

		frameReader.setFileSource(wav);
	}


	public void start(){
		processorThread.start();
		frameReader.start();
	}


	public static void main(String[] args) throws Exception{
		InputStream is = StreamingAcousticFrontEnd.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		ParameterSheet context = new ParameterSheetBuilder()
				.addParametersFromClass(MelFilter.class)
				.setParameter(MelFilter.HZ_, 8000)
				.setFrameLenInMs(1).build();
		
		System.out.println(context.getInteger(ParameterSheetBuilder.PARAM_FRAME_LEN_SAMPLE));
		System.out.println(context.getInt(BaseParams.FRAME_LEN_SAMPLE));

		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);

//		fe.setWav(wav);
//		fe.start();
	}

}
