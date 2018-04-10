package de.tarent.octopus.jmx;

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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.extensions.OctopusExtension;
import de.tarent.octopus.request.Octopus;

public class OctopusManagement implements OctopusExtension {
    private static Logger logger = Logger.getLogger(OctopusManagement.class.getName());

    private List<OctopusModuleManagement> modules = new LinkedList<OctopusModuleManagement>();

    public OctopusManagement() {
        super();
    }

    public void initialize(Object params) {
        if (!(params instanceof Map)) {
            logger.log(Level.SEVERE, "JMX extension parameter is not a map!");
        }

        if (!((Map) params).containsKey("octopus") || !((Map) params).containsKey("config")) {
            logger.log(Level.SEVERE, "JMX extension needs parameter 'octopus' and parameter 'config'");
        }

        Octopus octopus = (Octopus) ((Map) params).get("octopus");
        TcCommonConfig commonconfig = (TcCommonConfig) ((Map) params).get("config");

        // initialize octopus core MBean
        try {
            modules.add(new OctopusModuleManagement(octopus, commonconfig, "octopus"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing JMX for octopus core.", e);
        }

        // initialize module specific MBeans
        Iterator iter = commonconfig.getExistingModuleNames();
        String module = null;
        while (iter.hasNext()) {
            try {
                module = (String) iter.next();
                modules.add(new OctopusModuleManagement(octopus, commonconfig, module));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error initializing JMX for module " + module, e);
            }
        }
    }

    /**
     * Starts the management thread by getting a connection to a running
     * JMX server (or creating a new server) and publishing the management
     * information to the server.
     */
    public void start() {
        for (OctopusModuleManagement module : modules) {
            try {
                module.start();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error starting JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
        }
    }

    /**
     * Shuts down the JMX server by unregistering the MBean from the
     * running server.
     */
    public void stop() {
        for (OctopusModuleManagement module : modules) {
            try {
                module.stop();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error stopping JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
        }
    }
}
