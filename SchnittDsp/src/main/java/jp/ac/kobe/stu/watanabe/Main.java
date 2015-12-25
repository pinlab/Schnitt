package jp.ac.kobe.stu.watanabe;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import info.pinlab.pinsound.WavClip;

/**
 * This prigram is set the wav files and return the fft and mfcc value
 * 
 * @author snoopy
 * @version 1.0
 * @since 2015
 */
public class Main {
	public static void main(String[] args) throws Exception {
		final String HOME;
		final String filePath = "workspace/schnitt/SchnittDsp/src/main/resources/jp/ac/kobe/stu/watanabe/test.wav";
		final String wavFileIn;

		final int fftn = 512;
		final int ch = 26;
		final String windowType = "Hunning";
		final int stepLength = 10; /* ms */
		final int windowLength = 20; /* ms */
		final int hz;
		boolean running = true;

		/*
		 * Read wav file from a directory
		 */

		HOME = System.getProperty("user.home");
		wavFileIn = new File(HOME, filePath).getPath();
		WavClip wav = new WavClip(wavFileIn);
		File fileIn = new File(wavFileIn);

		System.err.println(wavFileIn);
		/*
		 * Get sampling rate
		 */
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
		hz = (int) audioInputStream.getFormat().getSampleRate();

		/*
		 * Set AcousticFrontEndFactory
		 */
		AcousticFrontEndFactory factory = new AcousticFrontEndFactoryImp(fftn, ch, windowType, windowLength, stepLength,
				hz);
		AcousticFrontEnd fe = factory.setFftN(fftn).setMfccCh(ch).setWindowType(windowType)
				.setWindowLength(windowLength).setStepLength(stepLength).build();

		/*
		 * Set Integer Samples
		 */
		int[] intSamples = wav.toIntArray();
		fe.setSamples(intSamples);
		int ix = 0;

		/*
		 * Loop Return Acoustic Values
		 */
		while (running) {
			double[] fftArr = fe.getFft();
			double[] mfccArr = fe.getMfcc();
			running = fe.next();

			 ix ++;
			 for(int j =0; j<fftArr.length; j++){
			 System.out.println("[ix:" + (ix + 1) + "]" + "FFT: " +
			 fftArr[j]);
			 }
			
			 for(int j =0; j<mfccArr.length; j++){
			 System.out.println( "[ix:" + (ix + 1) + "]"+ "MFCC: " +
			 mfccArr[j]);
			 }
		}
	}
}