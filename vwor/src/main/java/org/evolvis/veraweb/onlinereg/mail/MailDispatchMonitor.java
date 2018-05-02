package org.evolvis.veraweb.onlinereg.mail;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */

import org.jboss.logging.Logger;

import javax.mail.Address;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

public class MailDispatchMonitor implements TransportListener, ConnectionListener {

    private static final Logger LOGGER = Logger.getLogger(MailDispatchMonitor.class);
    private StringBuilder sb = new StringBuilder();

    private static String message(TransportEvent e) {
        final StringBuilder sb = new StringBuilder();
        switch (e.getType()) {
        case TransportEvent.MESSAGE_DELIVERED:
            sb.append("MESSAGE_DELIVERED");
            break;
        case TransportEvent.MESSAGE_NOT_DELIVERED:
            sb.append("MESSAGE_NOT_DELIVIERED");
            break;
        case TransportEvent.MESSAGE_PARTIALLY_DELIVERED:
            sb.append("MESSAGE_PARTIALLY_DELIVERED");
            break;
        default:
            sb.append("MESSAGE_NOT_DELIVIERED");
            break;
        }
        sb.append(":\n");
        appendAddresses(sb, "invalid", e.getInvalidAddresses());
        appendAddresses(sb, "valid/sent", e.getValidSentAddresses());
        appendAddresses(sb, "valid/unsent", e.getValidUnsentAddresses());
        sb.append("\n");
        return sb.toString();
    }

    private static void appendAddresses(StringBuilder sb, String label, Address[] addresses) {
        if (addresses != null && addresses.length > 0) {
            sb.append(label);
            sb.append(": ");
            boolean first = true;
            for (Address address : addresses) {
                sb.append(address.toString());
                if (!first) {
                    sb.append(", ");
                }
                first = false;
            }
            sb.append("\n");
        }
    }

    private void debug(ConnectionEvent e) {
        LOGGER.debug(e);
    }

    private void info(TransportEvent e) {
        sb.append(message(e));
    }

    private void warn(TransportEvent e) {
        LOGGER.warn(message(e));
        sb.append(message(e));
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void opened(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void disconnected(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void closed(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void messageDelivered(TransportEvent e) {
        info(e);
    }

    @Override
    public void messageNotDelivered(TransportEvent e) {
        warn(e);
    }

    @Override
    public void messagePartiallyDelivered(TransportEvent e) {
        warn(e);
    }
}
