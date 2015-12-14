package info.pinlab.schnitt.io;
import static org.junit.Assert.*;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataManagerImplTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadFile() throws Exception {
		InputStream is = DataManagerImplTest.class.getResourceAsStream("1392739_ay2015-uw-pre-TaskSet05_006_1__20150824-150818_.TextGrid");
		System.out.println(is);
		assertTrue(is!=null);
		
//		throw new RuntimeException("not yet implemented");
	}

}
