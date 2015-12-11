package jp.ac.kobe.stu.watanabe;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.pinsound.WavClip;
import jp.ac.kobe.stu.watanabe.AcousticFrontEnd;
import jp.ac.kobe.stu.watanabe.AcousticFrontEndFactory;
import jp.ac.kobe.stu.watanabe.AcousticFrontEndFactoryImp;

public class mfccSampleTest {
//	Default
	static String currentPath = System.getProperty("user.dir");
	private static BufferedReader br;
	static String Snd = "src/test/testResource/snd/test1.wav";
	static int [] intTestSamples;
	
	
	static String fftFile = "src/test/testResource/mfcc_samples.txt";
	static ArrayList<Double> mfccExpectedAry;
	static double [] mfccTestSamples;

	
	static AcousticFrontEndFactory factory;
	static AcousticFrontEnd fe;
	static int fftn = 512;
	static int ch   = 26;
	static String windowType = "Hunning";
	static int stepLength = 10;
	static int winLen = 20;
	static int hz = 16000;
	
	
	@BeforeClass
	public static void readFftFile() throws Exception{
		try{
			String fileName = new File(currentPath, fftFile).getPath();
			br   = new BufferedReader(new FileReader(fileName));
		
			mfccExpectedAry = new ArrayList<Double>();
			String value = br.readLine();
		
			while(value !=null){
				double doubleValue = Double.parseDouble(value);
				mfccExpectedAry.add(doubleValue);
				value = br.readLine();
			}

			br.close();
		}catch(FileNotFoundException e){
			System.out.println("IOE ERROR");
		}			
	}
	
	@Before
	public void frontEndSetUp() throws Exception {
		factory = new AcousticFrontEndFactoryImp(fftn, ch, windowType, winLen, stepLength, hz);
		fe = factory
				.setFftN(fftn)
				.setMfccCh(ch)
				.setWindowType(windowType)
				.build()
				;
		
		String testWavPath = new File(currentPath, Snd).getPath();
		WavClip wav = new WavClip(testWavPath);
		intTestSamples = wav.toIntArray();
		
		}
	@Test
	public void testMfccSamples() {
		int [] intSamplesTemp = new int [fftn];
		System.arraycopy(intTestSamples , 0, intSamplesTemp, 0, fftn);
		
		fe.setSamples(intSamplesTemp);
		double [] mfccTestSamples = fe.getMfcc();
		System.out.println(mfccTestSamples.length);

		for(int i = 0; i<fftn/2; i++){
			double expected = mfccExpectedAry.get(i);
			double obs = mfccTestSamples[i];
			
			assertEquals(expected, obs, 0.00001);
		}
	}
}
