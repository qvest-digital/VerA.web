package de.tarent.commons.config;

import java.io.InputStream;
import java.net.URISyntaxException;

import org.w3c.dom.Document;

import de.tarent.commons.config.ConfigManager.DocumentUnavailableException;
import de.tarent.commons.config.ConfigManager.Loader;
import de.tarent.commons.config.ConfigManager.Scope;

/**
 *
 * A loader capable of loading documents in classpath over a class loader.
 *
 * @author Fabian K&ouml;ster (f.koester@tarent.de) tarent GmbH Bonn
 *
 */
public class JARLoader extends Loader {

	Class applicationClass;

	JARLoader(String variant, Class applicationClass) {
		super(variant);

		this.applicationClass = applicationClass;
	}

	/**
	 * @see de.tarent.commons.config.ConfigManager.Loader#getDocument(de.tarent.commons.config.ConfigManager.Scope, java.lang.String)
	 */
	protected Document getDocument(Scope scope, String docName)
			throws DocumentUnavailableException {

		// FIXME: replace this hardcoded path
		String basePath = "/de/tarent/walendar/config/";

		if(scope == Scope.USER)
			throw new ConfigManager.DocumentUnavailableException("", "Scope \"user\" currently not supported in JAR-Loader");

		InputStream is = applicationClass.getResourceAsStream(basePath+scope.toString().toLowerCase()+"/"+docName);

		if(is == null)
			throw new RuntimeException("InputStream is null, cannot resolve \""+basePath+scope.toString().toLowerCase()+"/"+docName+"\"");

		try	{
			return XmlUtil.getParsedDocument(is, applicationClass.getResource(basePath+scope.toString().toLowerCase()+"/").toURI().toString());
		}
		catch (XmlUtil.Exception xmlue)	{
			throw new ConfigManager.DocumentUnavailableException("", "xml parsing error in \""+basePath+scope.toString().toLowerCase()+"/"+docName+"\"");
		} catch (URISyntaxException e) {
			throw new ConfigManager.DocumentUnavailableException("", "xml parsing error in \""+basePath+scope.toString().toLowerCase()+"/"+docName+"\"");
		}
	}

	/**
	 * @see de.tarent.commons.config.ConfigManager.Loader#isStoringSupported()
	 */
	protected boolean isStoringSupported() {
		return false;
	}
}
