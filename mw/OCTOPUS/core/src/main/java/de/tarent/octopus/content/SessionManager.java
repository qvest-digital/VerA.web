package de.tarent.octopus.content;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
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
import java.util.Iterator;
import java.util.Map;

/**
 * Session-Manager
 *
 * Sollte beim Laden eines Worker initalisiert werden
 * und in der pre (und post) Methode-Verwendet werden.
 *
 * Durchsucht Content und Request nach neuen den
 * Parametern und speichert diese in der Session ab.
 * Sollte der Parameter nicht übergeben werden, wird
 * der Wert aus der Session geladen bzw. der Default-Wert
 * genommen.
 *
 * @author Heiko Ferger
 * @see #add(String, Object)
 * @see #sync(TcAll)
 */
public class SessionManager {
    private final Map _enabledVars = new HashMap();

    /**
     * @deprecated Die Liste der erlauben variablen sollte nicht gelöscht werden
     */
    public void clear() {
        _enabledVars.clear();
    }

    /**
     * Funktion zum registrieren der erlaubten Session-Variablen
     * es ist nicht möglich den Wert (Defaultwert) der erlaubten Variable
     * ein zweites mal zu ändern.
     *
     * Erlaubte Variable ist dann also nach definition "final"
     *
     * @param name Key für den Request, Content und die Session.
     * @param def  Default-Wert.
     */
    public void add(String name, Object def) {
        if (!_enabledVars.containsKey(name)) {
            _enabledVars.put(name, def);
        }
    }

    public void sync(TcAll all) {
        Iterator it = _enabledVars.keySet().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();

            if (all.contentContains(name)) {
                all.setSession(name, all.contentAsObject(name));
            } else if (all.requestContains(name)) {
                all.setContent(name, all.requestAsObject(name));
                all.setSession(name, all.requestAsObject(name));
            } else if (all.sessionAsObject(name) != null) {
                all.setContent(name, all.sessionAsObject(name));
            } else {
                all.setContent(name, _enabledVars.get(name));
            }
        }
    }

    /**
     * Speichert alle Registrierten Variablen in der HashMap 'callByName' Dient
     * als erweiterung es VTL auch auf variablen per "Name" zugreifen (dies in
     * der Session registriert sind) meistens werden diese Variablen in Velocity
     * generich erzeugt z.b. Tabelle_[Navigation]
     */
    public void setCallByName(TcAll all) {
        HashMap callByName = new HashMap();
        Iterator it = _enabledVars.keySet().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();
            if (all.sessionAsObject(name) != null) {
                callByName.put(name, all.sessionAsObject(name));
            }
        }
        all.setContent("callByName", callByName);
    }

    public String toString() {
        return super.toString() + " " + _enabledVars;
    }
}
