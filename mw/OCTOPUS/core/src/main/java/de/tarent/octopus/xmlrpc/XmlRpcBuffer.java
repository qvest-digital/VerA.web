package de.tarent.octopus.xmlrpc;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2018 Dominik George (d.george@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlrpc.Base64;

import de.tarent.octopus.util.Xml;

/**
 * Diese Klasse kapselt einen StringBuffer, in dem eine XML-RPC-Antwort erstellt
 * wird.
 *
 * @author mikel
 */
public class XmlRpcBuffer {
    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor initialisiert den inneren StringBuffer mit einem XML-Prolog.
     */
    public XmlRpcBuffer() {
        buffer.append(PROLOG_START).append(UTF8_CANONICAL).append(PROLOG_END);
    }

    //
    // öffentliche Methoden
    //

    /**
     * Diese Methode liefert die kodierte XML-RPC-Antwort.
     */
    public ByteBuffer toByteBuffer() {
        Charset charset = Charset.forName(UTF8_JAVA);
        return charset.encode(buffer.toString());
    }

    /**
     * Diese Methode fügt eine vollständige XML-RPC-Antwort an.
     *
     * @param result Antwortparameter; wenn dieser null ist, ist die Antwort leer.
     * @throws IllegalArgumentException Wenn <code>null</code> im Parameter eingefügt werden soll,
     *                                  wird diese Ausnahme geworfen, da dies nicht nach der <a
     *                                  href="http://xml-rpc.com/spec">XML-RPC Specifikation </a>)
     *                                  erlaubt ist.
     */
    public void appendResponse(Object result) {
        startElement("methodResponse");
        if (result != null) {
            startElement("params");
            startElement("param");
            appendObject(result);
            endElement("param");
            endElement("params");
        }
        endElement("methodResponse");
    }

    /**
     * Diese Methode fügt eine XML-RPC-Beschreibung eines Java-Objekts ein.
     *
     * @param object Das einzufügende Objekt.
     * @throws IllegalArgumentException Wenn <code>null</code> als Objekt eingefügt werden soll,
     *                                  wird diese Ausnahme geworfen, da dies nicht nach der <a
     *                                  href="http://xml-rpc.com/spec">XML-RPC Specifikation </a>)
     *                                  erlaubt ist.
     */
    public void appendObject(Object object) {
        startElement("value");
        if (object == null) {
            throw new IllegalArgumentException("XML-RPC unterstützt keine null-Objekte");
        } else if (object instanceof String) {
            buffer.append(Xml.escape((String) object));
        } else if (object instanceof Integer) {
            appendDataElement("int", object);
        } else if (object instanceof Boolean) {
            appendDataElement("boolean", ((Boolean) object).booleanValue() ? "1" : "0");
        } else if (object instanceof Double || object instanceof Float) {
            appendDataElement("double", object);
        } else if (object instanceof Date) {
            appendDataElement("dateTime.iso8601", dateFormatter.format((Date) object));
        } else if (object instanceof byte[]) {
            appendDataElement("base64", new String(Base64.encode((byte[]) object)));
        } else if (object instanceof Object[]) {
            appendDataElement(Arrays.asList((Object[]) object));
        } else if (object instanceof Collection) {
            appendDataElement((Collection) object);
        } else if (object instanceof Map) {
            appendDataElement((Map) object);
        } else {
            throw new RuntimeException("nicht unterstützter Java-Typ: " + object.getClass());
        }
        endElement("value");
    }

    /**
     * Diese Methode fügt eine XML-RPC-Fehlermeldung ein.
     *
     * @param code    Fehlercode
     * @param message Fehlermitteilung
     */
    public void appendError(int code, String message) {
        Map errorMap = new HashMap();
        errorMap.put("faultCode", new Integer(code));
        errorMap.put("faultString", message);
        startElement("methodResponse");
        startElement("fault");
        appendObject(errorMap);
        endElement("fault");
        endElement("methodResponse");
    }

    /**
     * Diese Methode startet ein Element. Dieses muss mit
     * {@link #endElement(String)}beendet werden.
     *
     * @param element Name des Elements
     */
    public void startElement(String element) {
        buffer.append('<').append(element).append('>');
    }

    /**
     * Diese Methode beendet ein Element. Dieses muss mit
     * {@link #startElement(String)}gestartet worden sein.
     *
     * @param element Name des Elements
     */
    public void endElement(String element) {
        buffer.append("</").append(element).append('>');
    }

    //
    // Hilfsmethoden
    //

    /**
     * Diese Methode fügt ein einfaches Datenelement ein. Diese Methode führt
     * kein XML-Escaping durch!
     *
     * @param type   Typenstring
     * @param object Objekt, das als einfaches Datenobjekt einzufügen ist.
     */
    void appendDataElement(String type, Object object) {
        assert type != null;
        assert object != null;
        startElement(type);
        buffer.append(object);
        endElement(type);
    }

    /**
     * Diese Methode fügt eine Collection als XML-RPC-Array an.
     *
     * @param collection Kollektion, die einzufügen ist.
     */
    void appendDataElement(Collection collection) {
        assert collection != null;
        startElement("array");
        startElement("data");
        Iterator itList = collection.iterator();
        while (itList.hasNext()) {
            appendObject(itList.next());
        }
        endElement("data");
        endElement("array");
    }

    /**
     * Diese Methode fügt eine Map als XML-RPC-Struct an.
     *
     * @param map Map, die einzufügen ist.
     */
    void appendDataElement(Map map) {
        assert map != null;
        startElement("struct");
        Iterator itMap = map.entrySet().iterator();
        while (itMap.hasNext()) {
            Map.Entry entry = (Entry) itMap.next();
            startElement("member");
            startElement("name");
            buffer.append(Xml.escape(entry.getKey().toString()));
            endElement("name");
            appendObject(entry.getValue());
            endElement("member");
        }
        endElement("struct");
    }

    //
    // Konstanten
    //
    final static String PROLOG_START = "<?xml version=\"1.0\" encoding=\"";

    final static String PROLOG_END = "\"?>";

    final static String UTF8_JAVA = "UTF8";

    final static String UTF8_CANONICAL = "UTF-8";

    final static String DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss";

    //
    // Membervariablen
    //
    /**
     * In diesem Buffer wird die XML-RPC-Antwort erstellt
     */
    StringBuffer buffer = new StringBuffer();

    /**
     * Dieser Formatierer erzeugt Datumsdarstellungen der Form
     * <code>yyyyMMdd'T'HH:mm:ss</code>.
     */
    DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
}
