package de.tarent.commons.web;

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

import de.tarent.commons.utils.StringTools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class for dealing with parameters (key/value-pairs) passed over http-requests.
 * Use this class, if you want to keep user-specific information for more then one request
 * without using sessions.
 * <p>
 * If you're using GET as http-request-method, use encodeUrl() for all urls that
 * target pages that also need the user-specific data.
 * <p>
 * In case of the POST-method, getHiddenHTMLFormFields() deliveres fields
 * to be used in HTML-Forms containing the information.
 *
 * @author Tim Steffens
 */
public class Parameters extends LinkedHashMap {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3577853279726861257L;

    /**
     * The default encoding to be used for the parameters
     */
    private String defaultEncoding = "UTF-8";

    /**
     * The expected length for each key or value (used for estimating the size of the used StringBuffers)
     */
    public final static int EXPECTED_PARAMETER_LENGTH = 40;

    /**
     * true, iff the parameters should be encoded using the URLEncoder.encode() method, when generating
     * urls and hidden form fields.
     */
    private boolean needsEncoding = true;

    private String namePrefix = "";
    private String nameSuffix = "";

    public Parameters(Map additionalParameters) {
        super(additionalParameters);
    }

    public Parameters() {
        super();
    }

    /**
     * Attaches the contained parameters to {@code url} and returns it.
     *
     * @throws UnsupportedEncodingException If the encoding applied to the string is not supported.
     */
    public String encodeUrl(String url) throws UnsupportedEncodingException {
        if (isEmpty()) {
            return url;
        } else {
            boolean putSeparator;
            StringBuffer urlBuffer = new StringBuffer(this.keySet().size() * EXPECTED_PARAMETER_LENGTH * 2);
            urlBuffer.append(url.trim());

            if (url.indexOf("?") == -1) {    // if the url does not contain ?, we assume no parameters have been attached yet.
                urlBuffer.append("?");
                putSeparator = false;
            } else { // otherwise we assume there already is a parameterlist and just append it.
                putSeparator = true;
            }

            Iterator keysIterator = this.keySet().iterator();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();

                if (putSeparator) {
                    urlBuffer.append("&amp;");
                } else {
                    putSeparator = true;
                }

                urlBuffer.append(encodeName(key) + "="
                  + encodeValue(this.get(key)));
            }

            return urlBuffer.toString();
        }
    }

    /**
     * Returns hidden HTML forms fields containing all parameters
     *
     * @throws UnsupportedEncodingException If the encoding applied to the string is not supported.
     */
    public String getHiddenHTMLFormFields() throws UnsupportedEncodingException {
        StringBuffer fields = new StringBuffer(this.keySet().size() * (40 + (EXPECTED_PARAMETER_LENGTH * 2)));

        Iterator keysIterator = this.keySet().iterator();
        while (keysIterator.hasNext()) {
            String key = (String) keysIterator.next();
            fields.append("<input type=\"hidden\" name=\"" + encodeName(key)
              + "\" value=\"" + encodeValue(this.get(key)) + "\">" + StringTools.LINE_SEPARATOR);
        }

        return fields.toString();
    }

    private String encodeName(String name) throws UnsupportedEncodingException {
        if (name == null) {
            name = "";
        }
        if (needsEncoding) {
            return namePrefix + URLEncoder.encode(URLDecoder.decode(name, defaultEncoding), defaultEncoding) + nameSuffix;
        } else {
            return namePrefix + name + nameSuffix;
        }
    }

    private String encodeValue(Object value) throws UnsupportedEncodingException {
        if (needsEncoding) {
            return value == null ? "" : URLEncoder.encode(URLDecoder.decode(value.toString(), defaultEncoding), defaultEncoding);
        } else {
            return value == null ? "" : value.toString();
        }
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public boolean isNeedsEncoding() {
        return needsEncoding;
    }

    public void setNeedsEncoding(boolean needsEncoding) {
        this.needsEncoding = needsEncoding;
    }

    /**
     * Decodes all values contained in this map using @code{URLDecoder.decode()}.
     */
    public void decodeAll() throws UnsupportedEncodingException {
        Iterator it = keySet().iterator();
        while (it.hasNext()) {
            String currentKey = (String) it.next();
            String decodedValue = URLDecoder.decode((String) get(currentKey), defaultEncoding);
            put(currentKey, decodedValue);
        }
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
}
