package info.pinlab.snd.fe;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.fe.FrameProvider.AudioFrameConsumer;
import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;

/**
 * 
 * Calculates acoustic features in streams. 
 * In future: parallel, native support + GPU accelerated
 * 
 * @author Gabor Pinter
 *
 */
public class StreamingAcousticFrontEnd implements AudioFrameConsumer{
	public static final Logger LOG = LoggerFactory.getLogger(StreamingAcousticFrontEnd.class);
	
	private final ConcurrentLinkedDeque<Frame> inDeque;
	private final ConcurrentLinkedDeque<DoubleFrame> outDeque;

	int frameArrSize;
	private final ParamSheet context;
	private final double sampleMax;
	private int frameN = 0;
	private final FrameProvider frameReader; 
	private final int frameShiftInSample; 

	Thread processorThread;

	private final FrameProcessor [] pipeline;

	private FeatureSink sink = null;
	
	
	
	public StreamingAcousticFrontEnd(ParamSheet context){
		this.context = context;

		frameShiftInSample = context.get(FEParam.FRAME_LEN_SAMPLE)/2;
		
		frameReader = new FrameProvider(this.context);
		frameReader.setAudioFrameConsumer(this);
		
		sampleMax = (Math.pow(2, (this.context.get(FEParam.BYTE_PER_SAMPE)*8)))/2;
		System.out.println(sampleMax);
		
		inDeque = new ConcurrentLinkedDeque<Frame>();
		outDeque = new ConcurrentLinkedDeque<DoubleFrame>();

		pipeline = new FrameProcessor[1];
		try {
			//-- INIT frame processors here!

			HanningWindower hannWindower = FrameProcessorFactory.create(HanningWindower.class, this.context);
//			MelFilter melFilter = FrameProcessorFactory.create(MelFilter.class, this.context);
//			FrameProcessorFactory.create(Fft.class, this.context);
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
	
	public void setSink(FeatureSink sink){
		this.sink = sink;
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
//					System.out.println("> " + (frame.getStartSampleIx()/(double)hz));
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
				
				//-- not DoubleFrame: (must be end frame)
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
		//-- set  dummy consumer
		if(sink==null){
			LOG.info("Adding dummy sink");
			sink = new FeatureSink(){
				@Override
				public void add(DoubleFrame feature){	}
			};
		}
		
		processorThread.start();
		frameReader.start();
	}


	public static void main(String[] args) throws Exception{
		InputStream is = StreamingAcousticFrontEnd.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		ParamSheet context = new ParamSheetBuilder()
				.addParametersFromClass(MelFilter.class)
				.setAudioFormat(wav.getAudioFormat())
				.setFrameLenInMs(50).build();
		

		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);

		fe.setWav(wav);
		fe.start();
	}

}
