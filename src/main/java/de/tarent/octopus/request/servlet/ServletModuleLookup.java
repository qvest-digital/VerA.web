package de.tarent.octopus.request.servlet;

/*
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

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.resource.Resources;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class ServletModuleLookup implements TcModuleLookup {
	private final Log logger = LogFactory.getLog(ServletModuleLookup.class);

    /** Octopus servlet context */
	private final ServletContext servletContext;

	/** Octopus servlet */
	private final OctopusServlet octopusServlet;

	/** Octopus common config */
	private final TcCommonConfig commonConfig;

	/**
	 * @param servletContext
	 * @param octopusServlet
	 * @param commonConfig
	 */
	ServletModuleLookup(ServletContext servletContext, OctopusServlet octopusServlet, TcCommonConfig commonConfig) {
		this.servletContext = servletContext;
		this.octopusServlet = octopusServlet;
		this.commonConfig = commonConfig;
	}

	TcEnv getEnvironment() {
		return commonConfig.getEnvironment();
	}

	ServletContext getServletContext() {
		return servletContext;
	}

	public File getModulePath(String module) {
		Map modules = (Map)getEnvironment().getValueAsObject(TcEnv.KEY_MODULES);
		if (modules != null) {
			// Find modules parameters by module name
			// or use the default behind the '*'.
			Map parameters = (Map)modules.get(module);
			if (parameters == null) {
				parameters = (Map)modules.get("*");
				if (parameters == null) {
					logger.warn(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_NOT_FOUND", module, parameters));
					return null;					
				} else {
					logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_DEFAULT_PARAMETERS", module, parameters));
				}
			} else {
				logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_FOUND", module, parameters));
			}
			
			String source = (String)parameters.get(TcEnv.KEY_MODULE_SOURCE);
			if (source == null || source.length() == 0) {
				logger.warn(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_NO_SOURCE_PARAMETER", module));
				return null;
			} else {
				source = source.replaceAll("\\*", module);
			}
			
			logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_SOURCE", module, source));
			if (source.startsWith(TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX)) {
				return getModuleByServletContext(source.substring(TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX.length()));
			} else if (source.startsWith(TcEnv.VALUE_MODULE_SOURCE_FILE_PREFIX)) {
				String sourcePath = source.substring(TcEnv.VALUE_MODULE_SOURCE_FILE_PREFIX.length());
				if (new File(sourcePath).isAbsolute()) {
					return new File(sourcePath);
				} else {
					String octopusPath = getServletContext().getRealPath("");
					return new File(octopusPath, sourcePath);
				}
			} else {
				logger.error(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_ILLEGAL_SOURCE", module, source));
				return null;
			}
		} else {
			// Default behavior when no modules are configured.
			if (!module.startsWith("/"))
				module = "/" + module;
			logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_NOT_FOUND", module));
			logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_SOURCE", module, TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX + module));
			return getModuleByServletContext(module);
		}
	}

	public File getModuleByServletContext(String module) {
		ServletContext moduleContext = null;
		if (module.equals(octopusServlet.webappContextPathName))
			moduleContext = servletContext;
		else {
			moduleContext = servletContext.getContext(module);
		}
		
		if (moduleContext == null) {
			logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		String realPath = moduleContext.getRealPath("/OCTOPUS/");
		if (realPath == null || realPath.length() == 0) {
			logger.info(Resources.getInstance().get("REQUESTPROXY_LOG_NO_MODULE_CONTEXT", module));
			return null;
		}
		
		return new File(realPath);
	}
}
