package jp.ac.kobe.stu.watanabe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import info.pinlab.pinsound.WavClip;
import jp.ac.kobe.stu.watanabe.AcousticFrontEnd;
import jp.ac.kobe.stu.watanabe.AcousticFrontEndFactory;
import jp.ac.kobe.stu.watanabe.AcousticFrontEndFactoryImp;


public class FftSampleTest {
//	Default
	static String currentPath = System.getProperty("user.dir");
	private static BufferedReader br;
	static String Snd = "src/test/testResource/snd/test1.wav";
//	static String Snd = "src/test/testResource/snd/test2.wav";

//	For Int_Tests
	static String intFile = "src/test/testResource/int_samples.txt";
	static ArrayList<Integer> intExpectedAry;
	static int [] intTestSamples;

//  For Preemphed Samples
	static String preemphedFile = "src/test/testResource/preemph_samples.txt";	
	static ArrayList<Double> preemphedExpectedAry;
	static double [] preemphedTestSamples;

//  For Windowed samples
	static String windowedFile  = "src/test/testResource/windowed_samples.txt";
	static ArrayList<Double> windowedExpectedAry;
	static double [] windowesTestSamples;
	
//	For fft samples
	static String fftFile = "src/test/testResource/fft_samples.txt";
	static ArrayList<Double> fftExpectedAry;
	static double [] fftTestSamples;
	
	static AcousticFrontEndFactory factory;
	static AcousticFrontEnd fe;
	static int fftn = 512;
	static int ch   = 26;
	static String windowType = "Hunning";
	static int winLen = 20;
	static int stepLength = 10;
	static int hz = 16000;
	
	
	
	
	@BeforeClass
	public static void readIntfile() throws Exception {
		try{
			String fileName = new File(currentPath, intFile).getPath();
			br = new BufferedReader(new FileReader(fileName));
			
			intExpectedAry = new ArrayList<Integer>(); 
			String value = br.readLine();
			while(value != null){
				int intValue = Integer.parseInt(value);
				intExpectedAry.add(intValue);
				
				value = br.readLine();								
			}
			
			br.close();
			
		}catch(FileNotFoundException e){
			System.out.println("IOE ERROR");
		}
	}

	
	@BeforeClass
	public static void readPreemphedFile() throws Exception {
		try{			
			String fileName = new File(currentPath, preemphedFile).getPath(); 
			br = new BufferedReader(new FileReader(fileName));
	
			preemphedExpectedAry = new ArrayList<Double>();
			String value = br.readLine();
	
			while (value != null){
				double doubleValue = Double.parseDouble(value);
				preemphedExpectedAry.add(doubleValue);
		
				value = br.readLine();
			}
		
			br.close();
		}catch(FileNotFoundException e){
			System.out.println("IOE ERROR");
		
		}		
	}
	
	
	@BeforeClass
	public static void readWindowFile() throws Exception{
		try{
			String fileName = new File(currentPath, windowedFile).getPath();
			br = new BufferedReader(new FileReader(fileName));
			
			windowedExpectedAry = new ArrayList<Double>();
			String value = br.readLine();
			
			while(value!=null){
				double doubleValue = Double.parseDouble(value);
				windowedExpectedAry.add(doubleValue);
				
				value = br.readLine();
			}
			
			br.close();
			
		}catch(FileNotFoundException e){
				System.out.println("IOE ERROR");
				
			}
		}
	
	@BeforeClass
	public static void readFftFile() throws Exception{
		try{
			String fileName = new File(currentPath, fftFile).getPath();
		
			br   = new BufferedReader(new FileReader(fileName));
		
			fftExpectedAry = new ArrayList<Double>();
			String value = br.readLine();
		
			while(value !=null){
				double doubleValue = Double.parseDouble(value);
				fftExpectedAry.add(doubleValue);
			
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
				.setWindowLength(winLen)
				.setWindowType(windowType)
				.setStepLength(stepLength)
				.setHz(hz)
				.build()
				;
		
		String testWavPath = new File(currentPath, Snd).getPath();
		
		WavClip wav = new WavClip(testWavPath);
		intTestSamples = wav.toIntArray();
		
		}


	@Test
	public void testIntegerSamples() throws IOException {
		// Check size;
		assertEquals(intExpectedAry.size(), intTestSamples.length);

		// Check integers
		for(int i = 0; i<intTestSamples.length; i++){
			int expected = intExpectedAry.get(i);
			int obs = intTestSamples[i];

			assertEquals("different Integer", expected, obs);
		}
	}


//	@Test
//	public void testPreemphedSamples() throws IOException{
//		int [] intSamplesTemp = new int [fftn]; 
//		System.arraycopy(intTestSamples, 0, intSamplesTemp, 0, fftn);
//			
//		fe.writeSamples(intSamplesTemp);
//			
//		preemphedTestSamples = fe.readFft();
//		assertEquals("Not Set FFTN", fftn, preemphedTestSamples.length);
//
//		for(int i =0; i<fftn; i++){
//			double expected = preemphedExpectedAry.get(i);
//			double obs = preemphedTestSamples[i];
//				
//			assertEquals("Different number", expected, obs, 0.00001);
//			// Rounding Error = 0.00001
//		}
//	}
	
	
//	@Test
//	public void testWindonedSamples() throws IOException{
//		int [] intSamplesTemp =  new int [fftn];
//		System.arraycopy(intTestSamples, 0, intSamplesTemp, 0, fftn);
//			
//		fe.writeSamples(intSamplesTemp);
//		double [] windowedTestSamples = fe.readFft();
//		
//		for(int i = 0; i<fftn; i++){
//			double expected = windowedExpectedAry.get(i);
//			double obs = windowedTestSamples[i];
//			assertEquals(expected, obs, 0.00001);
//		}
//	}
	
	
	@Test
	public void testFftSamples() throws IOException{
		fe.setSamples(intTestSamples);
		double [] fftTestSamples = fe.getFft();
		System.err.println(fftTestSamples.length);

		for(int i = 0; i<fftn/2; i++){
			double expected = fftExpectedAry.get(i);
			double obs = fftTestSamples[i];
			
			System.err.println("exp: " + expected);
			System.err.println("obs: " + obs);

			assertEquals(expected, obs, 0.0001);
		}
	}
}
