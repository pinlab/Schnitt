package info.pinlab.snd.fe;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import info.pinlab.snd.fe.ParamSheet.ParamSheetBuilder;

public class ParamSheetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Ignore
	@Test
	public void writeTest(){
		ParamSheet sheet = new ParamSheetBuilder()
				.set(FEParam.HZ, 4000).build();
		assertTrue(sheet.get(FEParam.HZ)==4000);
	}

	
	@Test
	public void overWriteTest(){
		ParamSheetBuilder builder = new ParamSheetBuilder()
				.addParametersFromClass(MelFilter.class)
				.set(MelFilter.HZ, 5000)
				.set(FEParam.HZ, 4000)
				.set(MelFilter.HZ, 7000);
		assertTrue(builder.get(FEParam.HZ)==7000);
		ParamSheet sheet = builder.build(); 
		assertTrue(sheet.get(FEParam.HZ)==7000);
	}

	
	@Test
	public void frameLenTest(){
		ParamSheetBuilder builder = new ParamSheetBuilder()
				.addParametersFromClass(MelFilter.class)
				.set(FEParam.HZ, 8000)
				.set(FEParam.FRAME_LEN_MS, 10)
				.set(FEParam.BYTE_PER_SAMPE, 2);
		assertTrue(builder.get(FEParam.FRAME_LEN_MS)==10);
		assertTrue("Frame len is '" + builder.get(FEParam.FRAME_LEN_SAMPLE) +"' (!=80)"  
		, builder.get(FEParam.FRAME_LEN_SAMPLE)==80);
		assertTrue(builder.get(FEParam.FRAME_LEN_BYTE)==160);
		ParamSheet sheet = builder.build(); 
		assertTrue(sheet .get(FEParam.FRAME_LEN_MS)==10);
		assertTrue(sheet .get(FEParam.FRAME_LEN_SAMPLE)==80);
		assertTrue(sheet .get(FEParam.FRAME_LEN_BYTE)==160);
	}
	
}
