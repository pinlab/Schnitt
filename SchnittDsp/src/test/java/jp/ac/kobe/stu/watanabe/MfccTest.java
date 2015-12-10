package jp.ac.kobe.stu.watanabe;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.pinsound.WavClip;

public class MfccTest {

	static String CWD = System.getProperty("user.dir");
	static String wavFileName = "src/test/resource/snd/test1.wav";	
	static String mfccExpFile = "src/test/resource/test_mfcc.txt";
	static ArrayList<Double> mfccExpArr;
	static double [] mfccObsArr;
	
	static AcousticFrontEndFactory factory;
	static AcousticFrontEnd fe;
	static int FFTN = 512;
	static int MFCC_CH = 26;
	static String WINDOW = "Hunning";
	static int winLen = 20;
	static int stepLength = 10;
	static int HZ = 16000;
	
	static String absPathOfWav = new File(CWD, wavFileName).getPath();
	static int[] intWavSamples;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try{
			String pathOfMfccExpFile = new File(CWD, mfccExpFile).getPath();
			BufferedReader br = new BufferedReader(new FileReader(pathOfMfccExpFile));
			System.err.println(absPathOfWav);
			String value = br.readLine();
			mfccExpArr = new ArrayList<Double>();
		
			while(value != null){
				mfccExpArr.add(Double.parseDouble(value));
				value =  br.readLine();
			}
			br.close();
		}catch(FileNotFoundException e){
			System.err.println("IOE exception");
		}
	}

	
	@Before
	public void frontEndSetUp() throws Exception {
		factory = new AcousticFrontEndFactoryImp(FFTN, MFCC_CH, WINDOW, winLen, stepLength, HZ);
		fe = factory
				.setFftN(FFTN)
				.setMfccCh(MFCC_CH)
				.setWindowLength(winLen)
				.setWindowType(WINDOW)
				.setStepLength(stepLength)
				.setHz(HZ)
				.build()
				;
		WavClip wav = new WavClip(absPathOfWav);
		intWavSamples = wav.toIntArray();
		
		fe.setSamples(intWavSamples);
	}

	@Test
	public void test() {
		double [] obsMfccArr = fe.getMfcc();
		for(int i =0;i<obsMfccArr.length;i++){
			double expected = mfccExpArr.get(i);
			double obs = obsMfccArr[i];
			System.err.println("obs: " + obs + " [" +i +"]");
			System.err.println("exp: " + expected + "　[" +i +"]");
			assertEquals(expected, obs, 0.001);
			
		}

	}

}
