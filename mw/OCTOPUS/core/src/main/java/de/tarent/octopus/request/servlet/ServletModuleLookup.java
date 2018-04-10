package de.tarent.octopus.request.servlet;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcModuleLookup;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcEnv;
import de.tarent.octopus.resource.Resources;
import org.apache.commons.logging.Log;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;

/**
 * Diese Klasse liefert dem Octopus notwendige Daten.
 */
class ServletModuleLookup implements TcModuleLookup {
    private static final Log logger = LogFactory.getLog(ServletModuleLookup.class);

    /**
     * Octopus servlet context
     */
    private final ServletContext servletContext;

    /**
     * Octopus servlet
     */
    private final OctopusServlet octopusServlet;

    /**
     * Octopus common config
     */
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
        Map modules = (Map) getEnvironment().getValueAsObject(TcEnv.KEY_MODULES);
        if (modules != null) {
            // Find modules parameters by module name
            // or use the default behind the '*'.
            Map parameters = (Map) modules.get(module);
            if (parameters == null) {
                parameters = (Map) modules.get("*");
                if (parameters == null) {
                    logger.warn(
                            Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_NOT_FOUND",
                                    module, parameters));
                    return null;
                } else {
                    logger.info(
                            Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_DEFAULT_PARAMETERS",
                                    module, parameters));
                }
            } else {
                logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_FOUND",
                        module, parameters));
            }

            String source = (String) parameters.get(TcEnv.KEY_MODULE_SOURCE);
            if (source == null || source.length() == 0) {
                logger.warn(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_NO_SOURCE_PARAMETER",
                        module));
                return null;
            } else {
                source = source.replaceAll("\\*", module);
            }

            logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_SOURCE", module, source));
            if (source.startsWith(TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX)) {
                return getModuleByServletContext(
                        source.substring(TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX.length()));
            } else if (source.startsWith(TcEnv.VALUE_MODULE_SOURCE_FILE_PREFIX)) {
                String sourcePath = source.substring(TcEnv.VALUE_MODULE_SOURCE_FILE_PREFIX.length());
                if (new File(sourcePath).isAbsolute()) {
                    return new File(sourcePath);
                } else {
                    String octopusPath = getServletContext().getRealPath("");
                    return new File(octopusPath, sourcePath);
                }
            } else {
                logger.error(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_ILLEGAL_SOURCE",
                        module, source));
                return null;
            }
        } else {
            // Default behavior when no modules are configured.
            if (!module.startsWith("/")) {
                module = "/" + module;
            }
            logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_PARAMETERS_NOT_FOUND", module));
            logger.info(Resources.getInstance().get("OCTOPUS_MODULELOOKUP_USE_SOURCE", module,
                    TcEnv.VALUE_MODULE_SOURCE_SERVLET_PREFIX + module));
            return getModuleByServletContext(module);
        }
    }

    public File getModuleByServletContext(String module) {
        ServletContext moduleContext = null;
        if (module.equals(octopusServlet.webappContextPathName)) {
            moduleContext = servletContext;
        } else {
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
