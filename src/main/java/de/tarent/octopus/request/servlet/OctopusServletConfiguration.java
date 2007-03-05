package de.tarent.octopus.request.servlet;

import java.io.File;
import java.util.prefs.Preferences;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.OctopusConfiguration;
import de.tarent.octopus.resource.Resources;
import de.tarent.octopus.util.DataFormatException;
import de.tarent.octopus.util.Xml;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class OctopusServletConfiguration implements OctopusConfiguration {
	/** OctopusServletConfiguration */
	private final OctopusServlet OctopusServletConfiguration;

	/**
	 * @param servlet
	 */
	OctopusServletConfiguration(OctopusServlet servlet) {
		OctopusServletConfiguration = servlet;
	}

	/**
	 * Diese Methode liefert zu einem Modulnamen die Konfiguration.
	 * 
	 * @param module
	 *            der Name des Moduls.
	 * @return Modulkonfiguration zu dem Modul. <code>null</code> steht hier
	 *         für ein nicht gefundenes Modul.
	 * @see de.tarent.octopus.request.OctopusConfiguration#getModuleConfig(String,
	 *      Preferences)
	 */
	public TcModuleConfig getModuleConfig(String module, Preferences modulePreferences) {
		ServletContext moduleContext = null;

		if (module.equals(OctopusServletConfiguration.webappContextPathName))
			moduleContext = OctopusServletConfiguration.getServletContext();
		else {
			moduleContext = OctopusServletConfiguration.getServletContext().getContext("/" + module);
		}
		
		if (moduleContext == null) {
			OctopusServlet.logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		return getModuleConfig(module, modulePreferences, moduleContext);
	}

	TcModuleConfig getModuleConfig(String module, Preferences modulePreferences, ServletContext moduleContext) {

		String realPath = moduleContext.getRealPath("/OCTOPUS/");
		if (realPath == null || realPath.length() == 0) {
			OctopusServlet.logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		File modulePath = new File(realPath);
		File configFile = new File(modulePath, "config.xml");
		if (!configFile.exists())
			configFile = new File(modulePath, "module-config.xml");
		if (!configFile.exists())
			return null;
		
		try {
			OctopusServlet.logger.debug(Resources.getInstance().get("REQUESTPROXY_LOG_PARSING_MODULE_CONFIG", configFile, module));
			Document document = Xml.getParsedDocument(Resources.getInstance().get("REQUESTPROXY_URL_MODULE_CONFIG", configFile.getAbsolutePath()));
			return new TcModuleConfig(module, modulePath, document, modulePreferences);
		} catch (SAXParseException se) {
			OctopusServlet.logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_SAX_EXCEPTION", new Integer(se.getLineNumber()), new Integer(se.getColumnNumber())), se);
		} catch (DataFormatException ex) {
			OctopusServlet.logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_FORMAT_EXCEPTION"), ex);
		} catch (Exception ex) {
			OctopusServlet.logger.error(Resources.getInstance().get("REQUESTPROXY_LOG_MODULE_PARSE_EXCEPTION"), ex);
		}
		
		return null;
	}
}
