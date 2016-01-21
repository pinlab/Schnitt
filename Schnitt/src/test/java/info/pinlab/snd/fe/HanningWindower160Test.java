package info.pinlab.snd.fe;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * @author Watanabe
 *
 */
public class HanningWindower160Test{
	public static Logger LOG = LoggerFactory.getLogger(HanningWindower160Test.class);

	static double error = 0.00001;
	static double [] exp;
	static double [] intSampArr;
	static int size = 160;
	static int frameLen = 10;
	static int hz = 16000;
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setting up the paramter context
		
		// Read the int arrary of wa file (test.wav) for test
		exp = new double[size];
		intSampArr = new double[size];
		
		try{
			LOG.info("Reading " + HanningWindower160Test.class.getResource("test_int_160_wav.txt").getPath());
			FileReader fr = new FileReader(HanningWindower160Test.class.getResource("test_int_160_wav.txt").getPath());
			BufferedReader br = new BufferedReader(fr);
			int ix = 0;
			String line;
			while((line = br.readLine()) !=null){
//				System.err.println("testInt: " + line + " ix: " + ix);
				intSampArr[ix] = Double.parseDouble(line);
				ix++;
			}
			br.close();
		}catch(FileNotFoundException e){
			System.err.println(e);
		}
		
		// Read the result of hanning windowning by python .
		
		try{
			LOG.info("Reading " + HanningWindower160Test.class.getResource("TestHanningWindower160.txt").getPath());
			FileReader fr = new FileReader(HanningWindower160Test.class.getResource("TestHanningWindower160.txt").getPath());
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
	public void testProcess() throws Exception{
		HanningWindower hannWindow = new HanningWindower(size);
		double [] obs = new double [size];
		obs = hannWindow.process(intSampArr);
		
//		for(int i=0; i<size; i++){
//			System.err.println("res: " 	+ obs[i] + " expected: " + exp[i]);
//		}
		
		assertArrayEquals(exp, obs, error);
	}
}