package de.tarent.octopus.request.servlet;

import java.net.MalformedURLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This lifecycle listener will be notified if a servlet context has just
 * been created and is available to service its first request,
 * or the servlet context is about to be shutdown.
 *
 * It will be used for realize that new octopus modules will be available.
 * So we can implement
 * the {@link Octopus#doAutostart(String, CommonConfig))} and
 * the {@link Octopus#doCleanup(String, CommonConfig)} also if one module
 * will loaded after the octopus webapplication. (Only in seperate
 * installations.)
 *
 * @author Christoph Jerolimov, tarent GmbH
 */
public class LifecycleListener implements ServletContextListener {
	/** Logger instance */
	private Log logger = LogFactory.getLog(LifecycleListener.class);

	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String module = getOctopusModule(servletContext);
		Object octopus = getOctopus();

		if (octopus == null) {
			logger.info(
					"Webapplication context '" + module + "' initialized, " +
					"but octopus is not available (yet).");
		} else {
			logger.info(
					"Webapplication context '" + module + "' initialized, " +
					"will be register it at internal octopus.");
			// TODO
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String module = getOctopusModule(servletContext);
		Object octopus = getOctopus();

		if (octopus == null) {
			logger.info(
					"Webapplication context '" + module + "' destroyed, " +
			"but octopus is not available (at the moment).");
		} else {
			logger.info(
					"Webapplication context '" + module + "' destroyed, " +
					"will be unregister it at internal octopus.");
			// TODO
		}
	}

	protected String getOctopusModule(ServletContext servletContext) {
		// This return the display name of the module!
		//return servletContext.getServletContextName()

		try {
			// Return in tomcat 5.5 a URL like this 'jndi:/localhost/modulename/'
			// where the path includes the full part right of the colon.
			String module = servletContext.getResource("/").getPath();
			if (module == null || module.length() <= 1)
				return null;
			while (module.endsWith("/"))
				module = module.substring(0, module.length() - 1);
			if (module.lastIndexOf("/") != -1)
				module = module.substring(module.lastIndexOf("/") + 1);
			return module;
		} catch (MalformedURLException e) {
			logger.warn(e.getLocalizedMessage(), e);
			return null;
		}
	}

	protected Object getOctopus() {
		Context context = getContext();
		if (context == null) {
			return null;
		}
		try {
			return context.lookup("octopus/instance");
		} catch (NamingException e) {
			logger.info("JNDI context available. Can not find octopus instannce.");
			return null;
		}
	}

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
			logger.info("No JNDI context available. Can not find octopus instance.", e);
			return null;
		}
	}
}
