package jp.ac.kobe.stu.watanabe;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.ac.kobe.stu.watanabe.Dct;

public class DctTest {

	static String CWD;
	private static BufferedReader br;

	static double [] obsArr;
	static ArrayList<Double> expArr;
	static String testFileName = "src/test/testResource/test_dct.txt";
	
	static double [] testArr = 
		{1.0000, 1.0000, 1.0000, 1.0000, 2.0000, 2.0000, 2.0000, 2.0000, 2.0000};
	static double [] dctArr;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try{
			CWD = System.getProperty("user.dir");
			String absFileName = new File(CWD, testFileName).getPath();
			System.err.println(absFileName);
			br = new BufferedReader(new FileReader(absFileName));
		
			expArr = new ArrayList<Double>();
			String value = br.readLine();
		
			while(value != null){
				expArr.add(Double.parseDouble(value));
				value = br.readLine();
			}
			
			br.close();
		}catch(FileNotFoundException e){
			System.err.println("IOE ERROR happens");
		}
		
	}
	

	@Before
	public void setUp() throws Exception {

		Dct dct = new Dct(testArr.length);
		dctArr = dct.transform(testArr);
	}

	@Test
	public void test() {
		for(int i=0; i<testArr.length; i++){
			double expected = expArr.get(i);
			double obs       = dctArr[i];
			
			System.err.println("exp: " + expected);
			System.err.println("obs: " + obs);
			
			assertEquals(expected, obs, 0.00001);
		}
	}
}
