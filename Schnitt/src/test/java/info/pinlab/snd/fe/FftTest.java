package info.pinlab.snd.fe;

import static org.junit.Assert.assertArrayEquals;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.Sys;

/**
 * 
 * @author Watanabe
 *
 */
public class FftTest {
	static double error = 0.00001;
	static double [] expectedAmpArr;
	static double [] obsAmpArr;
	static int fftN = 512;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expectedAmpArr = new double [fftN/2];			
		// Read the result of fft by python
		try{
			FileReader fr = new FileReader(
					FftTest.class.getResource("TestFft.txt").getPath());
			BufferedReader br = new BufferedReader(fr);
			int ix = 0;
			String line;
			while((line=br.readLine())!=null){
				expectedAmpArr[ix] = Double.parseDouble(line);
				ix++;
			}
			br.close();
		}catch(FileNotFoundException e){
			System.err.println(e);
		}		
	}

	
	@Test
	public void testProcess() throws Exception{
		double [] hann = HanningWindower.HANN_320;
		Fft fft = new Fft(fftN, hann.length);
		obsAmpArr = fft.process(hann);		
//		for(double d: obsAmpArr){
//			System.out.println(d);
//		}
		assertArrayEquals(expectedAmpArr, obsAmpArr, error);
	}
}
