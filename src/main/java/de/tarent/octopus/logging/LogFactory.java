package de.tarent.octopus.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * A simple factory which can be used to get a log instance.
 * This is due to the automatic log choosing in
 * {@link org.apache.commons.logging.LogFactory}
 * 
 */
public class LogFactory {

	static Properties properties;
	static InputStream in;
	static Object value;
	
	static {		
		setProperties();		
		value = properties.get("logging.api");
	}
	
	public static Log getLog(Class clazz) {
		if (useJdkLogger())
			return new Jdk14Logger(clazz.getName());
		else if (useLog4jLogger())
			return new Log4JLogger(clazz.getName());
		else if (useCommonsLogger())
			return org.apache.commons.logging.LogFactory.getLog(clazz);
		else if (useSimpleLog())
			return new SimpleLog(clazz.getName());
		else
			return org.apache.commons.logging.LogFactory.getLog(clazz);
    }	
	
	public static void setProperties() {
		in = LogFactory.class.getResourceAsStream("tarent-logging.properties");		
		properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static boolean useJdkLogger() {
		return (value.equals("jdk"));
	}
	
	static boolean useLog4jLogger() {
		return (value.equals("log4j"));
	}
	
	static boolean useCommonsLogger() {
		return (value.equals("commons"));
	}
	
	static boolean useSimpleLog() {
		return (value.equals("simplelog"));
	}
	
}
