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

import de.tarent.octopus.request.TcRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse enthält Hilfsmethoden für ContentWorker.
 *
 * @author mikel
 */
public class TcContentWorkerHelper {
    /**
     * Diese Methode liefert einen Parameter zurück, wobei zunächst im
     * Content, dann im Request gesucht wird.
     *
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key       Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static Object getParam(TcRequest tcRequest, TcContent tcContent, String key) {
        Object param = tcContent.getAsObject(key);
        return (param == null) ? tcRequest.getParam(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als Liste, wobei zunächst im
     * Content, dann im Request gesucht wird.
     *
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key       Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static List getParamAsList(TcRequest tcRequest, TcContent tcContent, String key) {
        Object param = tcContent.get(key);
        if (param == null) {
            param = tcRequest.getParam(key);
        }
        List result = null;
        if (param instanceof List) {
            result = (List) param;
        } else if (param instanceof Object[]) {
            result = Arrays.asList((Object[]) param);
        } else {
            result = new ArrayList();
            if (param != null) {
                result.add(param);
            }
        }
        return result;
    }

    /**
     * Diese Methode liefert einen Parameter als String, wobei zunächst im
     * Content, dann im Request gesucht wird.
     *
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key       Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static String getParamAsString(TcRequest tcRequest, TcContent tcContent, String key) {
        String param = tcContent.getAsString(key);
        return (param == null) ? tcRequest.get(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als int, wobei zunächst im
     * Content, dann im Request gesucht wird.
     *
     * @param tcRequest    Request-Objekt
     * @param tcContent    Content-Objekt
     * @param key          Schlüssel des gesuchten Parameters
     * @param defaultValue default-Wert für den Parameter
     * @return Wert des Parameters
     */
    public static int getParamAsInt(TcRequest tcRequest, TcContent tcContent, String key, int defaultValue) {
        String stringVal = getParamAsString(tcRequest, tcContent, key);
        if (stringVal != null) {
            try {
                return Integer.parseInt(stringVal);
            } catch (NumberFormatException e) {
            }
        }
        return defaultValue;
    }

    /**
     * Diese Methode liefert einen Parameter als boolean, wobei zunächst im
     * Content, dann im Request gesucht wird.
     *
     * @param tcRequest    Request-Objekt
     * @param tcContent    Content-Objekt
     * @param key          Schlüssel des gesuchten Parameters
     * @param defaultValue default-Wert für den Parameter
     * @return Wert des Parameters
     */
    public static boolean getParamAsBoolean(
      TcRequest tcRequest,
      TcContent tcContent,
      String key,
      boolean defaultValue) {
        String stringVal = getParamAsString(tcRequest, tcContent, key);
        if (stringVal != null) {
            return Boolean.valueOf(stringVal).booleanValue();
        }
        return defaultValue;
    }

    /**
     * Dieser private Konstruktor verbietet das instanziieren dieser Klasse.
     */
    private TcContentWorkerHelper() {
        super();
    }
}
