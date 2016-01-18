package info.pinlab.snd.fe;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;
import info.pinlab.snd.fe.Windower.WindowType;

public class HanningWindowerTest{
	static ParamSheet context;
	static double error = 0.0001;
	static String testExpectedFile = 
			"/home/snoopy/workspace/schnitt/Schnitt/src/test/resources/info/pinlab/snd/fe/TestHanningWindower320.txt";
	static String testIntSampleFile = 
			"/home/snoopy/workspace/schnitt/Schnitt/src/test/resources/info/pinlab/snd/fe/test_int.txt";
	static double [] exp;
	static double [] intSampArr;
	static int size;
	static int frameLen = 20;
	static int hz = 16000;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Read the int arrary of wa file (test.wav) for test
		context = new ParamSheetBuilder()
				.set(FEParam.FRAME_PROCESSORS, "windowning")
				.setFrameLenInMs(frameLen)
				.setHz(hz)
				.setWinType(WindowType.HANNING)
				.build();
		
		size = context.get(FEParam.FRAME_LEN_SAMPLE);
		System.err.println("size: " + size);
		
		exp = new double[size];
		intSampArr = new double[size];
		
		try{
			FileReader fr = new FileReader(testIntSampleFile);
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
			FileReader fr = new FileReader(testExpectedFile);
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

	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testProcess() throws Exception{
		HanningWindower hannWindow = new HanningWindower(context);
		double [] obs = new double [size];
		obs = hannWindow.process(intSampArr);
			
		for(int i=0; i<size; i++){
			System.err.println("res: " + obs[i]);
		}
		
		assertArrayEquals(exp, obs, error);
	}
}