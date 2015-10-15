package info.pinlab.snd;

import javax.sound.sampled.AudioFormat;

import info.pinlab.pinsound.WavClip;

public class WavUtil {
	
	public static int[] getIntArray(WavClip wav){
		byte[] bytes = wav.getSamples();
		AudioFormat af = wav.getAudioFormat();
		int bytePerFrame = af.getSampleSizeInBits()/8 * wav.getAudioFormat().getChannels();
		boolean isMono = (af.getChannels() == 1);
		
		int [] samples = new int[bytes.length/bytePerFrame];
		int j = 0;
		
		for (int i = 0 ; i < bytes.length ; i += bytePerFrame){
			int sampleAsInt = 0;
			if(bytePerFrame == 1){ 
				sampleAsInt = (int) bytes[i];
			}else
			if(bytePerFrame == 2 && isMono){ 
				if(!af.isBigEndian() /* LittleEndian */ ){
//					System.out.println("LE -> Int");
					//-- Little Endian to signed integer --//
					sampleAsInt = (int) bytes[i+1];
					sampleAsInt <<= 8;
					sampleAsInt |= bytes[i] & 0x00FF  ;
				}else{
					//-- Big Endian to signed integer --//
//					System.out.println("BE -> Int");
					sampleAsInt = (int) bytes[i];
					sampleAsInt <<= 8;
					sampleAsInt |= bytes[i+1] & 0x00FF  ;
				}
			}
			samples[j++] = sampleAsInt;
		}
		bytes = null;
		return samples;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
