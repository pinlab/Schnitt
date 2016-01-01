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
		ParamSheet sheet = new ParamSheetBuilder()
				.addParametersFromClass(MelFilter.class)
				.set(MelFilter.HZ, 5000)
				.set(FEParam.HZ, 4000)
				.set(MelFilter.HZ, 7000)
				.build();
		assertTrue(sheet.get(FEParam.HZ)==7000);
	}

}
