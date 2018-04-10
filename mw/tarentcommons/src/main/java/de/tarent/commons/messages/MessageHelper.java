package de.tarent.commons.messages;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageHelper {
	private static final int STATIC_PUBLIC_MODIFIER = 9;

	private MessageHelper() {
	}

	public static void init() {
		StackTraceElement ste[];
		try {
			throw new Exception();
		} catch (Exception e) {
			ste = e.getStackTrace();
		}
		boolean foundThisClass = false;
		for (int i = 0; i < ste.length; i++) {
			if (foundThisClass) {
				init(ste[i].getClassName());
				break;
			}
			if (ste[i].getClassName().equals(MessageHelper.class.getName()))
				foundThisClass = true;
		}
	}

	public static void init(Class clazz) {
		init(clazz.getName());
	}

	public static void init(String classname) {
		init(classname, classname.replace('.', '/'));
	}

	public static void init(String classname, String bundlename) {
		try {
			Class clazz = Class.forName(classname);
			String source = "Source path is not available.";
			ResourceBundle bundle = null;

			try {
				bundle = ResourceBundle.getBundle(bundlename);
				source = getResourceName(clazz.getClassLoader(), bundlename, bundle.getLocale());
			} catch (MissingResourceException e) {
				System.err.println(e);
				e.printStackTrace();
				// nothing here, please note that bundle can be null.
			} catch (NullPointerException e) {
				System.err.println(e);
				e.printStackTrace();
				// nothing here, source path not found is default message.
			}

			Field fields[] = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getModifiers() == STATIC_PUBLIC_MODIFIER) {
					String key = fields[i].getName();
					String message;
					if (bundle != null) {
						try {
							message = bundle.getString(key);
						} catch (MissingResourceException e) {
							message = "No message for key '" + key + "' in resource bundle '" + bundlename + "' found.";
						}
					} else {
						message = "No message for key '" + key + "' found, resource bundle '" + bundlename + "' is missing.";
					}

					if (fields[i].getType().equals(String.class)) {
						fields[i].set(null, message);
					} else if (fields[i].getType().equals(Message.class)) {
						fields[i].set(null, new MessageImpl(key, source, message));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getResourceName(ClassLoader loader, String bundlename, Locale locale) {
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(bundlename.replace('.', '/'));
			if (locale.toString().length() != 0)
				buffer.append("_").append(locale.toString());
			buffer.append(".properties");

			URL resource = loader.getResource(buffer.toString());
			if (resource == null)
				throw new NullPointerException("Resource for \"" + buffer + "\" can not be null.");
			return resource.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
