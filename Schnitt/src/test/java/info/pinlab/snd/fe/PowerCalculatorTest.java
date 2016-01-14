package info.pinlab.snd.fe;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PowerCalculatorTest {
	@Test
	public void testProcess() throws Exception {
		PowerCalculator pow = new PowerCalculator(null);
		double [] results = pow.process(new double []{10});
		assertTrue(results.length==1);
		assertTrue(results[0]==100);
		
		results = pow.process(new double []{10, 5});
		assertTrue(results.length==1);
		for(double d: results){
			System.out.println(d);
		}
		assertTrue(results[0]==100);
	}

}
