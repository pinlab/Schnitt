package info.pinlab.snd.dsp;

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

	
	int frameArrSize;
	private final AcousticContext context;
	
	static class Frame{
		int id;
		int nextId;
		int prevId;
		
		final double [] samples;
		double[] features; 
		
		Frame(double [] s){
			samples = s;
		}
	}
	
	
	
	
	public StreamingAcousticFrontEnd(AcousticContext context){
		this.context = context;
		
		AudioFrameReader afr = new AudioFrameReader(context);
		afr.setAudioFrameConsumer(this);
		
	}
	
	
	
	public void consume(int[] samples){
		
	}
	
	
	
	
	public static void main(String[] args) {
	}
}
