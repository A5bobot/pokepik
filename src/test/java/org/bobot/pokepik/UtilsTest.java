package test.java.org.bobot.pokepik;

import java.util.Properties;

import main.java.org.bobot.pokepik.Utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void testGetProperties() {		
		Boolean isPropOK;	
		String strProps = "mongo";		
		Properties props = Utils.getProperties(strProps);
		isPropOK = ("localhost".equals(props.getProperty("vertx.mongo.host")));
		Assert.assertTrue(isPropOK);
	}

}
