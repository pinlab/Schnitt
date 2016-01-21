package info.pinlab.snd.fe;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HanningWindower320Test {
	public static Logger LOG = LoggerFactory.getLogger(HanningWindower320Test.class);

	static double error = 0.00001;
	static double [] exp;
	static double [] intSampArr;
	static int size = 320;
	static int frameLen = 20;
	static int hz = 16000;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Read the int arrary of wa file (test.wav) for test
		exp = new double[size];
		intSampArr = new double[size];
		
		try{
			LOG.info(HanningWindower320Test.class.getResource("test_int_320_wav.txt").getPath());
			FileReader fr = new FileReader(HanningWindower320Test.class.getResource("test_int_320_wav.txt").getPath());
			BufferedReader br = new BufferedReader(fr);
			int ix = 0;
			String line;
			while((line = br.readLine()) !=null){
				intSampArr[ix] = Double.parseDouble(line);
				ix++;
			}
			br.close();
			}catch(FileNotFoundException e){
				System.err.println(e);
			}

		// Read the result of hanning windowning by python .
		try{
			LOG.info(HanningWindower320Test.class.getResource("TestHanningWindower320.txt").getPath());
			FileReader fr = new FileReader(HanningWindower320Test.class.getResource("TestHanningWindower320.txt").getPath());
			BufferedReader br = new BufferedReader(fr);
			int ix = 0;
			String line;
			while((line = br.readLine())!=null){
//				System.err.println("testRes: " + line + " ix: " + ix);
				exp[ix] = Double.parseDouble(line);
				ix++;
			}
			br.close();
		}catch(FileNotFoundException e){
			System.err.println(e);
		}
	}


	@Test
	public void testProcess() throws Exception {
		HanningWindower hannWindow = new HanningWindower(size);
		double [] obs = new double [size];
		obs = hannWindow.process(intSampArr);
		
//		for(int i = 0; i<size; i++){
//			System.err.print("res: " + obs[i] + " expected: " + exp[i]);
//		}
//
		assertArrayEquals(exp, obs, error);
	}

}
