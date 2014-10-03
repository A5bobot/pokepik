package main.java.org.bobot.pokepik;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

	public static Properties getProperties (String strProp) {
		
		String propFile = null;
		
		if ("mongo".equals(strProp)) {
			propFile="ressources/mongo.properties";
		}
		
		if ("auth".equals(strProp)) {
			propFile="ressources/auth.properties";
		}
		
		Properties props = new Properties();
		
		InputStream input = null;
	    try {
			input = new FileInputStream(propFile);
			props.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return props;
	}
	
}
