package de.tarent.octopus.content;

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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Kontainer zur Speicherung der Daten die von den ContentWorkern besorgt wurden.
 * <br>
 * Der Container kann auch Fehlermeldungen aufnehmen.
 * <br>
 * Datenfelder, die hierin gespeichert werden, sollten einen zusammen gesetzten Key
 * bekommen, damit Konflikte durch gleiche Namen aus verschiedenen Kontexten vermieden werden.
 * <br>
 * Der Kontainer ist als Baum von Maps, Arrays und Strings organisiert. Um trotzdem einen
 * einfachen Zugriff darauf zu ermöglichen, unterstützen alle Methoden eine Punktnotation bei den Keys.
 * <br><br>
 * Beispiel: address.name bezeichnet den Wert, der in der Map address zu 'name' abgelegt ist.
 * <br>Beispiel: address.fon.2 bezeichnet der 2. Element des Array, das unter 'fon' in der Map address abgelegt ist.
 * <br><br>
 * Wenn ein Wert unter einem solchen Key abgelegt wird und die daruterliegende Struktur noch nicht existiert, wird sie
 * automatisch erstellt.
 * Bei dieser automatischen erstellung werden für jede Stufe immer Map angelegt, nie Arrays.
 * <br><br>
 * Da die benutzten Speicherstrukturen nicht mit null-Pointern umgehen können, wird überall darauf getestet.
 * Wenn ein key oder value ein null-Pointer ist, kehrt die Methode einfach zurück und meldet keinen Fehler.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcContent {
    /**
     * Enthält ein Kürzel, daß das Ergebniss der Aktion charakterisiert.
     */
    private String status;

    /**
     * Die Content Daten
     */
    private Map theContent;

    public Map getContent() {
        return theContent;
    }

    /**
     * Initialisiert den Content mit leeren Feldern und dem Status "ok".
     */
    public TcContent() {
        theContent = new LinkedHashMap();
        setStatus(TcContentWorker.RESULT_ok);
    }

    /**
     * Initialisiert den Content mit dem Status "error"
     * und setzt die Felder 'status.message', sowie 'status.detailMessage'
     * auf den Meldungen aus der Exception
     *
     * @param e Exception, deren Meldungen aufgetreten sind.
     */
    public TcContent(Exception e) {
        theContent = new LinkedHashMap();
        setError(e);
    }

    /**
     * Setzt dem Status "error"
     * und setzt die Felder 'status.message', sowie 'status.detailMessage'
     * auf den Meldungen aus der Exception
     *
     * @param e Exception, deren Meldungen aufgetreten sind.
     */
    public void setError(Exception e) {
        if (e == null) {
            setField("status.message", "Es ist ein unbekannter Fehler auf getreten!");
            setField("status.detailMessage", "Es ist ein unbekannter Fehler auf getreten!");
        } else {
            setField("status.detailMessage", "Es ist ein Fehler auf getreten: " + e);
            setField("status.exception", e);
            if (e.getMessage() != null) {
                setField("status.message", e.getMessage());
            } else {
                setField("status.message", e);
            }
        }
        setStatus("error");
    }

    /**
     * Setzt dem Status "error"
     * und setzt die Felder 'status.message', sowie 'status.detailMessage'
     * auf den Meldungen aus des Strings
     *
     * @param message Message, die gesetzt werden soll.
     */
    public void setError(String message) {
        setField("status.message", message);
        setField("status.detailMessage", message);
        setStatus("error");
    }

    /**
     * Setzt das Ergebniss der Aktion
     *
     * @param status Kürzel für die Aktion. (z.B. "ok" oder "error")
     */
    public void setStatus(String status) {
        this.status = status;
        setField("status.code", status);
    }

    /**
     * Gibt ein Kürzel zurück, daß das Ergebniss der Aktion charakterisiert.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Liefert die Keys der belegten Felder
     */
    public Iterator getKeys() {
        return theContent.keySet().iterator();
    }

    /**
     * Gibt ein Feld als String zurück.
     *
     * @param key Der Key des Fedes
     */
    public String getAsString(String key) {
        Object field = getAsObject(key);
        if (field != null) {
            return field.toString();
        } else {
            return null;
        }
    }

    /**
     * Gibt ein Feld als String zurück.
     * kurzform für getAsObject()
     *
     * @param key Der Key des Fedes
     */
    public Object get(String key) {
        return getAsObject(key);
    }

    /**
     * Gibt ein Feld als Object zurück.
     *
     * @param key Der Key des Feldes
     */
    public Object getAsObject(String key) {

        if (key == null || key.length() == 0) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(key, ".");
        String token = st.nextToken();
        Object node = theContent;
        Object newNode;
        int index;
        while (st.hasMoreTokens()) {

            if (node instanceof Map) {
                newNode = ((Map) node).get(token);
            } else if (node instanceof List) {
                index = 0;
                try { // Wenn der key nicht gültig ist, geben wir einfach zurück
                    index = Integer.parseInt(token);
                } catch (Exception e) {
                    return null;
                }
                if (index < 0 || index >= ((List) node).size()) {
                    return null;
                }
                newNode = ((List) node).get(index);
            } else {
                return null;
            }

            if (newNode == null) {
                return null;
            }

            node = newNode;
            token = st.nextToken();
        }

        if (node instanceof Map) {
            return ((Map) node).get(token);
        } else if (node instanceof List) {
            index = 0;
            try { // Wenn der key nicht gültig ist, geben wir einfach null zurück
                index = Integer.parseInt(token);
            } catch (Exception e) {
                return null;
            }
            if (index < 0 || index >= ((List) node).size()) {
                return null;
            }
            return ((List) node).get(index);
        } else {
            return null;
        }
    }

    /**
     * Setzt ein String Feld.
     *
     * @param key   Der Key, unter dem die Daten gespeichert werden sollen.
     * @param value Der Inhalt
     */
    public void setField(String key, String value) {
        setField(key, (Object) value);
    }

    /**
     * Setzt ein Feld von Maps.
     *
     * @param key  Der Key, unter dem die Daten gespeichert werden sollen.
     * @param data Die Daten
     */
    public void setField(String key, Map data) {
        setField(key, (Object) data);
    }

    /**
     * Setzt ein Feld mit einem Vector.
     *
     * @param key  Der Key, unter dem die Daten gespeichert werden sollen.
     * @param data Die Daten
     */
    public void setField(String key, List data) {
        setField(key, (Object) data);
    }

    /**
     * Setzt ein Feld mit einem Integer.
     *
     * @param key  Der Key, unter dem die Daten gespeichert werden sollen.
     * @param data Die Daten
     */
    public void setField(String key, Integer data) {
        setField(key, (Object) data);
    }

    /**
     * Setzen eines Feldes von einem beliebigen Typ.
     * Ist Private, da nur Lists, Maps und Strings gesetz werden sollen.
     * bei Fehlern wird einfach zurück gekehrt.
     */
    public void setField(String key, Object data) {
        if (key == null || "".equals(key)) {
            return;
        }

        StringTokenizer st = new StringTokenizer(key, ".");
        String token = st.nextToken();
        Object node = theContent;
        Object newNode;
        int index;

        while (st.hasMoreTokens()) {

            if (node instanceof Map) {
                newNode = ((Map) node).get(token);
                if (newNode == null) {
                    newNode = new LinkedHashMap();
                    ((Map) node).put(token, newNode);
                }
            } else if (node instanceof List) {
                index = 0;
                try { // Wenn der key nicht gültig ist, geben wir einfach zurück
                    index = Integer.parseInt(token);
                } catch (Exception e) {
                    return;
                }
                if (index < 0) {
                    return;
                }
                //                if (index >= ((List) node).size())
                //                     ((Vector) node).setSize(index + 1);
                newNode = ((List) node).get(index);
                if (newNode == null) {
                    newNode = new LinkedHashMap();
                    ((List) node).set(index, newNode);
                }
            } else {
                return;
            }

            node = newNode;
            token = st.nextToken();
        }

        if (node instanceof Map) {
            ((Map) node).put(token, data);
        } else if (node instanceof List) {
            index = 0;
            try { // Wenn der key nicht gültig ist, hängen wir einfach an.
                index = Integer.parseInt(token);
            } catch (Exception e) {
                index = ((List) node).size();
            }
            if (index < 0) {
                return;
            }
            //            if (index >= ((Vector) node).size())
            //                 ((Vector) node).setSize(index + 1);
            ((List) node).set(index, data);
        }
    }

    /**
     * String räpräsentation z.B. für Debugausgaben.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("TcContent:\n");
        sb.append("Bearbeitungsstatus Status: " + status + "\n");
        sb.append("Daten:\n" + toString("", theContent));

        return "" + sb;
    }

    private static String toString(String prefix, Object o) {

        if (o == null) {
            return "null";
        }

        StringBuffer sb = new StringBuffer();
        if (o instanceof Map) {
            sb.append("\n" + prefix + "{\n");
            Map theContent = (Map) o;
            for (Iterator e = theContent.keySet().iterator(); e.hasNext(); ) {
                Object key = e.next();
                Object val = theContent.get(key);
                sb.append("     " + prefix + key + " => " + toString(prefix + "     ", val));
            }
            sb.append("\n" + prefix + "}\n");
        } else if (o instanceof List) {
            sb.append("\n" + prefix + "[\n");
            List vector = (List) o;
            for (int i = 0; i < vector.size(); i++) {
                sb.append("     " + prefix + i + " => " + toString(prefix + "     ", vector.get(i)));
            }
            sb.append("\n" + prefix + "]\n");
        } else {
            sb.append(o.toString() + "\n");
        }

        return "" + sb;
    }
}
