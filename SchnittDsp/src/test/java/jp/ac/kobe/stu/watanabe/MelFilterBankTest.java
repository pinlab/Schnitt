package jp.ac.kobe.stu.watanabe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.ac.kobe.stu.watanabe.MelFilterBank;

public class MelFilterBankTest {

	static String CWD;
	static BufferedReader br;
	static String testFileName = "src/test/testResource/test_melfilter-bank.txt";
	
	static double [] testValueArr;
	static ArrayList<Double> expArr;
	
	static int FFTN = 512;
	static int FS   = 16000;
	static int MFCC_CH = 26;
	static double []mfcArr = new double [MFCC_CH];
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try{
			CWD = System.getProperty("user.dir");
			String absTestFileName = new File(CWD, testFileName).getPath();
			System.err.println(absTestFileName);
			br = new BufferedReader(new FileReader(absTestFileName));
			
			expArr = new ArrayList<Double>();
			String value = br.readLine();
			while(value != null){
				expArr.add(Double.parseDouble(value));
				value = br.readLine();
			}
			br.close();
		}catch(FileNotFoundException e){
			System.err.println("IOE Error!!");
			
		}
	}
		

	@Before
	public void setUp() throws Exception {
		testValueArr = new double[FFTN/2];
		for(int i =0; i<FFTN/2;i++){
			testValueArr[i] = (double) i;
			//System.err.println((double) i );
		}
		
		MelFilterBank mfb = new MelFilterBank(FFTN, FS, MFCC_CH);
		mfcArr = mfb.doMelFilterBank(testValueArr);
	}

	@Test
	public void test() {
		assertEquals(mfcArr.length, MFCC_CH);
		for(int i=0; i<MFCC_CH; i++){
			double expected = expArr.get(i);
			double obs       = mfcArr[i];
			
			System.err.println("exp: " + expected);
			System.err.println("mfccobs: " + obs);
			
			assertEquals(expected, obs, 0.00001);
		}
	}
}
