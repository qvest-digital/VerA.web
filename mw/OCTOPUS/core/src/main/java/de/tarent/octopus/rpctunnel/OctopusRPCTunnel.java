package de.tarent.octopus.rpctunnel;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
import de.tarent.octopus.request.Octopus;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Wrapper singleton class for class <code>de.tarent.octopus.rpctunnel.RPCTunnel</code> which
 * is part of a different library and must not be available.
 *
 * @author hendrik
 */
@Log4j2
public class OctopusRPCTunnel {
    private OctopusRPCListener listener = null;

    public static final String ROLE_OCTOPUS = "octopus";

    private static OctopusRPCTunnel octTunnel = null;

    private OctopusRPCTunnel(Octopus octopus, TcCommonConfig commonconfig) throws RPCTunnelUnavailableException {
        try {
            listener = new OctopusRPCListener(octopus, commonconfig);

            try {
                //Registration of the listener-implementation.
                //The class RPCTunnel may not be available so it's necessary to use reflection.
                //The call RPCTunnel.registerListener(listener, OCTOPUS_ROLE)
                //wouldn't allow to catch exeptions inside this class.

                Class tunnelc = Class.forName("de.tarent.octopus.rpctunnel.RPCTunnel");
                Class listc = Class.forName("de.tarent.octopus.rpctunnel.RPCListener");
                Method reg = tunnelc.getMethod("registerListener", new Class[] { listc, String.class });
                reg.invoke(this, new Object[] { listener, ROLE_OCTOPUS });
            } catch (Exception e) {
                throw new RPCTunnelUnavailableException();
            }
        } catch (NoClassDefFoundError e) {
            throw new RPCTunnelUnavailableException();
        }
    }

    /**
     * If class RPCTunnel is not in the classpath or this method was called before
     * then nothing is done.
     *
     * @param octopus
     * @param commonconfig
     */
    public static void createInstance(Octopus octopus, TcCommonConfig commonconfig) {
        if (!isAvailable()) {
            try { //Try to create an instance of OctopusRPCTunnel
                octTunnel = new OctopusRPCTunnel(octopus, commonconfig);
            } catch (RPCTunnelUnavailableException e) {
                octTunnel = null;
                logger.info("Octopus-RPC-tunnel is unavailable.");
            }
        }
    }

    public static OctopusRPCTunnel getInstance() {
        return octTunnel;
    }

    public Map execute(String myRole, String partnerRole, String module, String task, Map parameters) {
        //At this point it is ensured that class RPCTunnel is in the classpath
        return RPCTunnel.execute(myRole, partnerRole, module, task, parameters);
    }

    public static boolean isAvailable() {
        return octTunnel != null;
    }
}

class RPCTunnelUnavailableException extends Exception {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2463482051282677136L;

    public RPCTunnelUnavailableException() {
        super();
    }

    public RPCTunnelUnavailableException(String message) {
        super(message);
    }

    public RPCTunnelUnavailableException(Throwable cause) {
        super(cause);
    }

    public RPCTunnelUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
