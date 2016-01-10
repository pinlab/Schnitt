package info.pinlab.snd.fe;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.pinlab.pinsound.WavClip;
import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;
import info.pinlab.snd.trs.DoubleFrameTier;

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

	private final List<FrameProcessor> pipeline;

	private FeatureSink sink = null;


	static class DoubleFrameTierBuilder implements FeatureSink{
		private DoubleFrameTier tier ;
		CountDownLatch latch = new CountDownLatch(1);
		
		DoubleFrameTierBuilder(int hz, int frameSizeMs){
			tier = new DoubleFrameTier(hz, frameSizeMs);
		}
		
		@Override
		public void add(DoubleFrame frame) {
			tier.add(frame);
		}
		
		@Override
		public void end() {
			synchronized (this) {
				latch.countDown();
			}
		}
		
		public DoubleFrameTier getDoubleFrameTier(){
			try { latch.await();} 
			catch (InterruptedException ignore) {	}
			return tier;
		}
	}
	
	

	public StreamingAcousticFrontEnd(ParamSheet context){
		this.context = context;

		inDeque = new ConcurrentLinkedDeque<Frame>();
		pipeline = new ArrayList<FrameProcessor>(); 
		outDeque = new ConcurrentLinkedDeque<DoubleFrame>();

		//-- Frame producer: 
		frameShiftInSample = context.get(FEParam.FRAME_LEN_SAMPLE)/2;
		frameReader = new FrameProvider(this.context);
		frameReader.setAudioFrameConsumer(this);

		sampleMax = (Math.pow(2, (this.context.get(FEParam.BYTE_PER_SAMPE)*8)))/2;
		System.out.println(sampleMax);


		//-- INIT frame processors here!
		String procList = this.context.get(FEParam.FRAME_PROCESSORS);
		if(procList==null){
			LOG.error("No frame processor pipline is defined! Set param!" + FEParam.FRAME_PROCESSORS.getFqName());
		}else{
			String prevKey = "sample";
			for(String procClass : procList.split(":")){
				if(procClass!=null && !procClass.isEmpty()){
					try {
						FrameProcessor processor = FrameProcessorFactory.create(procClass, this.context);
						processor.setPredecessorKey(prevKey);
						pipeline.add(processor);
						prevKey = processor.getKey();
					} catch (ClassNotFoundException e){
						LOG.error("Not available class '" + procClass +"'");
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void setSink(FeatureSink sink){
		this.sink = sink;
	}


	/**
	 * 
	 * @return  the pipeline in order
	 */
	public List<FrameProcessor> getProcessorPipeline(){
		return (new ArrayList<FrameProcessor>(pipeline));
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
//			System.out.println("END! reched from frame processor");
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
//					outDeque.addFirst(dblFrame);
					sink.add(dblFrame);
					continue LOOP;
				}else
					if(frame instanceof EndFrame){
//						System.out.println("END! in sink writing!");
						sink.end();
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

		frameReader.setSource(wav);
	}

	public DoubleFrameTier getFrameTier(){
		if(sink instanceof DoubleFrameTierBuilder){
			return ((DoubleFrameTierBuilder)sink).getDoubleFrameTier();
		}
		return null;
	}
	


	public void start(){
		//-- set  dummy consumer
		if(sink==null){
			LOG.info("Adding dummy sink that builds a DoulbeFrameTier");
			sink = new DoubleFrameTierBuilder(
					context.get(FEParam.HZ), 
					context.get(FEParam.FRAME_LEN_MS));
		}
		
		processorThread.start();
		frameReader.start();
	}


	public static void main(String[] args) throws Exception{
		InputStream is = StreamingAcousticFrontEnd.class.getResourceAsStream("sample.wav");
		WavClip wav = new WavClip(is);

		String processorList = 
				HanningWindower.class.getName()
//				+ ":" + Fft.class.getName()
//				+ ":" + MelFilter.class.getName()
				;


		ParamSheet context = new ParamSheetBuilder()
				.set(FEParam.FRAME_PROCESSORS, processorList)
//				.addParametersFromClass(MelFilter.class)
				.setAudioFormat(wav.getAudioFormat())
				.setFrameLenInMs(50)
				.build();


		StreamingAcousticFrontEnd fe = new StreamingAcousticFrontEnd(context);

		fe.setWav(wav);
		fe.start();
		
		DoubleFrameTier tier = fe.getFrameTier();
		System.out.println(tier + "\t" + tier.size());
		for(Double t : tier.getTimeLabels()){
//			System.out.println(t);
			DoubleFrame frame = tier.getFrameAt(t);
			double [] arr = frame.getArray("sample");
			System.out.println(Arrays.toString(arr));
		}
	}

}
