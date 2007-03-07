package de.tarent.octopus.request.servlet;

import java.io.File;

import javax.servlet.ServletContext;

import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.resource.Resources;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class ServletModuleLookup implements TcModuleLookup {
	/** Octopus servlet */
	private final OctopusServlet octopusServlet;

	/** Octopus servlet context */
	private final ServletContext servletContext;

	/**
	 * @param servletContext
	 * @param octopusServlet
	 */
	ServletModuleLookup(ServletContext servletContext, OctopusServlet octopusServlet) {
		this.servletContext = servletContext;
		this.octopusServlet = octopusServlet;
	}

	OctopusServlet getOctopusServlet() {
		return octopusServlet;
	}

	ServletContext getServletContext() {
		return servletContext;
	}

	public File getModulePath(String module) {
		ServletContext moduleContext = null;
		if (module.equals(octopusServlet.webappContextPathName))
			moduleContext = getServletContext();
		else {
			moduleContext = getServletContext().getContext("/" + module);
		}
		
		if (moduleContext == null) {
			OctopusServlet.logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		String realPath = moduleContext.getRealPath("/OCTOPUS/");
		if (realPath == null || realPath.length() == 0) {
			OctopusServlet.logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		return new File(realPath);
	}
}
