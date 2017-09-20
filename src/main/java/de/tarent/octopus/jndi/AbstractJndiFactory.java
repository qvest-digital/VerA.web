package de.tarent.octopus.jndi;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

/**
 * Diese Klasse stellt eine JNDI-{@link ObjectFactory} zum Zugriff auf den aktuellen
 * Octopus-Kontext dar.<br>
 * Sie ist angelehnt an das Beispiel für das erzeugen spezialisierter Resource Factories
 * <a href="http://jakarta.apache.org/tomcat/tomcat-5.0-doc/jndi-resources-howto.html#Adding Custom Resource Factories">hier</a>
 * in der Tomcat-Dokumentation.<br>
 * Zur Nutzung muss diese Factory der JNDI-Machinerie bekannt gemacht werden. Analog
 * obigem Beispiel ist dies im Projekt octopus/webapp getan worden, entsprechende
 * Einträge stehen dort in den Dateien <code>octopus.xml</code> und <code>web.xml</code>
 * im Verzeichnis <code>webapp/WEB-INF</code>.
 */
public abstract class AbstractJndiFactory implements ObjectFactory {
	protected Log logger = LogFactory.getLog(getClass());

	/**
	 * Currently only support the apache tomcat (and maby more servlet container?)
	 * as JNDI context provider. See
	 * <code>http://tomcat.apache.org/tomcat-5.5-doc/jndi-resources-howto.html</code>
	 * for more informations about this configuration.
	 * 
	 * @return naming context
	 */
	protected Context getContext() {
		try {
			return (Context) new InitialContext().lookup("java:comp/env");
		} catch (NamingException e) {
			logger.info("No JNDI context available. Can not bind context '" + getLookupPath() + "'.", e);
			return null;
		}
	}
	
	public boolean bind() {
		Context context = getContext();
		if (context == null)
			return false;
		
		try {
			context.addToEnvironment(Context.OBJECT_FACTORIES, getClass().getName());
		} catch (NamingException e) {
			logger.warn(
					"Can not add current class '" + getClass().getName() + "' " +
					"to the object factory list.");
		}
		
		try {
			Object object = context.lookup(getLookupPath());
			if (object != null && object.getClass().getName().equals(getClass().getName())) {
				logger.info(
						"JNDI context available. " +
						"Context '" + getLookupPath() + "' already binded.");
				return true;
			} else if (object != null) {
				logger.info(
						"JNDI context available. " +
						"Wrong class '" + object.getClass().getName() + "' " +
						"for context '" + getLookupPath() + "' binded, will rebind it now.");
			} else {
				logger.info(
						"JNDI context available. " +
						"Context '" + getLookupPath() + "' not binded yet, will do it now.");
			}
		} catch (NamingException e) {
			logger.info(
					"JNDI context available. " +
					"Exception '" + e.getLocalizedMessage() + "' while lookup " +
					"context '" + getLookupPath() + "' catched, will rebind it now.");
		}
		
		try {
			context.rebind(getLookupPath(), this);
			logger.info("JNDI context '" + getLookupPath() + "' successful binded.");
			return true;
		} catch (NamingException e) {
			logger.info("JNDI context available, but can not bind context '" + getLookupPath() + "'.");
			return false;
		}
	}

	public boolean unbind() {
		Context context = getContext();
		if (context == null)
			return false;
		
		try {
			context.unbind(getLookupPath());
			logger.info("JNDI context '" + getLookupPath() + "' successful unbinded.");
			return true;
		} catch (NamingException e) {
			logger.error("JNDI context available, but can not unbind context '" + getLookupPath() + "'.", e);
			return false;
		}
	}

	abstract protected String getLookupPath();

	public Object getObjectInstance(Object object, Name name, Context context, Hashtable environment) throws Exception {
		return this;
	}
}
