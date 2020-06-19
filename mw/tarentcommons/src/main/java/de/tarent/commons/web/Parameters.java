package de.tarent.commons.web;

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
