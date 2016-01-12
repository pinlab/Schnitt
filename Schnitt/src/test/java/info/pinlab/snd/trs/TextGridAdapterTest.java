package info.pinlab.snd.trs;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

public class TextGridAdapterTest {

	
	

	
	
	
//	@Test
	public void testFromTextGridInputStream() throws Exception {
		String gridFileName = "sample.TextGrid";
		InputStream is = TextGridAdapterTest.class.getResourceAsStream(gridFileName);
		assertTrue(is!=null);

		LabelTier tier = TextGridAdapter.fromTextGrid(is);
		assertTrue(tier != null);
		
		assertTrue(tier.size() > 0);
		
		Double prev = tier.getFirstInterval();
		prev -= 1;
		for(Interval<String> inter : tier){
			assertTrue(prev < inter.startT);
			assertTrue(inter.label != null);
			if(!inter.label.isEmpty()){
				assertTrue(inter.label.charAt(0) != '\"');  //- if first quote removed
				assertTrue(inter.label.charAt(0) != ' ');  //- if space is removed
				assertTrue(inter.label.charAt(inter.label.length()-1) != ' ');  //- if final space is removed
				assertTrue(inter.label.charAt(inter.label.length()-1) != '\"');  //- if final quote removed
			}
		}
	}

}
