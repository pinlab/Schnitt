package info.pinlab.snd.fe;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HanningWindowerTest {
	static String testResult = 
			"/home/snoopy/workspace/schnitt/Schnitt/src/test/resources/info/pinlab/snd/fe/TestHanningWindower.txt";
	static String testInt = 
			"/home/snoopy/workspace/schnitt/Schnitt/src/test/resources/info/pinlab/snd/fe/test_int.txt";
	static double [] testResultArr;
	static double [] testArr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Read the int arrary of wav file (test.wav) for test
		try{
			File file = new File(testInt);
			FileReader filereader = new FileReader(file);
			int line = filereader.read();
			int ix = 0;
			while(line!=-1){
				line = filereader.read();
				testArr[ix] = (double) line;
				ix++;
			}
			filereader.close();
		}catch(FileNotFoundException e){
			System.err.println(e);
		}
		
		// Read the result of hanning windowning by python .
		try{
			File file = new File(testResult);
			FileReader filereader = new FileReader(file);
			int line = filereader.read();
			int ix = 0;
			while(line!=-1){
				line = filereader.read();
				testResultArr[ix] = (double) line;
				ix++;
			}
			filereader.close();
		}catch(FileNotFoundException e){
			System.err.println(e);
		}
	}

	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testProcess() throws Exception{
		double [] hann = HanningWindower.HANN_320;
		HanningWindower hannWindow = new HanningWindower(null);
		double result = hannWindow.filter(testArr);
		
		for(int i =0; i<hann.length;i++){
			AssertEquals(testResultArr[i], 0.1, 0.001);
		}
	}

}
