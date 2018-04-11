package de.tarent.octopus.rpctunnel;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RPCTunnel {
    private static Map listeners;
    private static Logger logger = Logger.getLogger(RPCTunnel.class.getName());

    // Registering default OctopusCallbackListener
    static {
        //RPCTunnel.registerListener(new OctopusCallbackListener(), DefaultRoles.ROLE_ACTIVEBPEL);
    }

    public static boolean registerListener(RPCListener listener, String role) {
        if (listeners == null) {
            listeners = new HashMap();
        }
        listeners.put(role, listener);
        logger.info("Listener " + role + " is registered by the RPC-tunnel.");
        return true;
    }

    public static boolean unregisterListener(RPCListener listener) {
        if (listeners == null) {
            return false;
        } else {
            boolean b = listeners.values().remove(listener);
            if (b) {
                logger.info("Listener is unregistered by the RPC-tunnel.");
            }
            return b;
        }
    }

    /**
     * This method must have the same signature as the method
     * <code>execute</code> in interface <code>RPCListener</code>.
     *
     * @param myRole
     * @param partnerRole
     * @param module
     * @param task
     * @param parameters
     * @return <code>null</code> if the addressee given by <code>partnerRole</code> is unknown.
     */
    public static Map execute(String myRole, String partnerRole, String module, String task, Map parameters) {
        RPCListener addressee = (RPCListener) listeners.get(partnerRole);
        if (addressee == null) {
            return null;
        }
        return addressee.execute(myRole, partnerRole, module, task, parameters);
    }
}
