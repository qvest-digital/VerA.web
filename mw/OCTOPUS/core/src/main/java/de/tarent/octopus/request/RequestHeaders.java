package de.tarent.octopus.request;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class holds request headers and provides generic access to any type of protocol headers.
 * Headers are hold in a list in which they are stored as a key value pair with the headers name as key
 * and any kind of representing header class as value. The headers are not directly stored and accessed
 * in a map structure because some protocols may allow multiple headers of the same type and name.
 * However this behaviour may be optimized in the implemting protocol header class.
 *
 * @author Jens Neumaier, tarent GmbH
 */
public class RequestHeaders {

    protected List headers;

    /**
     * In this default constructor a <code>List</code> of headers with key-value-pairs (Singleton-Maps)
     * as entry should be supplied. This key-value-pairs should identify the header's name
     * with its value or another <code>List</code> of subheaders structured identically.
     *
     * @param headers list of key-value-pairs for each header
     */
    public RequestHeaders(List headers) {
        this.headers = headers;
    }

    /**
     * Returns first header found by given name. You may find header types as constants in protocol specific
     * <code>RequestHeaders</code> implementations.
     *
     * @param headerName indentifier for the requested header
     * @return header as <code>Object</code>
     */
    public Object getHeaderAsObject(String headerName) {
        Iterator iter = headers.iterator();

        while (iter.hasNext()) {
            Map singletonMap = (Map) iter.next();
            if (singletonMap.containsKey(headerName)) {
                return singletonMap.get(headerName);
            }
        }

        return null;
    }

    /**
     * Returns first header found by given name. You may find header types as constants in protocol specific
     * <code>RequestHeaders</code> implementations.
     * This method should be overwritten in protocol specific implementations of this class.
     *
     * @param headerName indentifier for the requested header
     * @return header as <code>String</code>
     */
    public String getHeaderAsString(String headerName) {
        return getHeaderAsString(headerName).toString();
    }

    /**
     * Returns a list of headers found by given name. This method should be used if multiple headers of the same type may exist.
     * The returned list directly contains the specific class representation of the original header.
     *
     * @param headerName indentifier for the requested header
     * @return headers in a <code>List</code> of <code>Object</code>s
     */
    public List getHeadersAsList(String headerName) {
        List foundHeaders = new ArrayList(6);
        Iterator iter = headers.iterator();

        while (iter.hasNext()) {
            Map singletonMap = (Map) iter.next();
            if (singletonMap.containsKey(headerName)) {
                foundHeaders.add(singletonMap.get(headerName));
            }
        }

        return foundHeaders;
    }

    /**
     * Returns an <code>Iterator</code> over all request headers as key value pairs as stored stored in the headers list.
     * See class description for details.
     *
     * @return <code>Iterator</code> over request headers
     */
    public Iterator iterator() {
        return this.headers.iterator();
    }

    /**
     * Returns a set view of the keys respectively the names of the available headers.
     *
     * @return a set view of the header names (keys)
     */
    public Set keySet() {
        Set keySet = new HashSet(headers.size());

        Iterator iter = headers.iterator();
        while (iter.hasNext()) {
            keySet.addAll(((Map) iter.next()).keySet());
        }
        return keySet;
    }

    /**
     * Returns a collection view of the values of the available headers in their specific class representation.
     *
     * @return a collection of header values as <code>Object</code>s
     */
    public Collection values() {
        Collection values = new ArrayList(headers.size());

        Iterator iter = headers.iterator();
        while (iter.hasNext()) {
            values.addAll(((Map) iter.next()).values());
        }
        return values;
    }

    /**
     * Returns a collection view of the values of the available headers in their string representation if available.
     * To ensure this method is working properly in an extended class just overwrite the methods
     * {@link RequestHeaders#keySet()} and
     * {@link RequestHeaders#getHeaderAsString(String)}.
     *
     * @return a collection of header values as <code>String</code>s
     */
    public Collection stringValues() {
        Collection stringValues = new ArrayList(headers.size());

        Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            stringValues.add(this.getHeaderAsString((String) iter.next()));
        }
        return stringValues;
    }

    //
    //
    //

    /**
     * Returns the subheaders of a header as <code>RequestHeaders</code>.
     *
     * Because headers are often structured in hierachical orders with subheaders describing
     * sub-values of a specific header you may overwrite this method to get subheaders as <code>RequestHeaders</code>
     * for generic access. For this to work you just have to put another headers <code>List</code> structured in the same way as
     * your main headers as value of your header. Alternatively you can directly store a <code>RequestHeaders</code>
     * representation
     * as your headers value. This method will provide the subheaders as <code>RequestHeaders</code> if possible.
     *
     * @param headerName indentifier for the header to retrieve subheaders from
     * @return subheaders as <code>RequestHeaders</code>
     */
    public RequestHeaders getSubHeaders(String headerName) {
        Object possibleSubHeaders = getHeaderAsObject(headerName);
        // test if headers are found
        if (possibleSubHeaders != null) {
            // test if subheaders are given in a list of singleton maps
            if (possibleSubHeaders instanceof List) {
                // instantiate the subheaders with its original implementing class to ensure overwritten
                // access methods will be used and the object can be used as expected
                try {
                    Constructor constructorImplementingClass = null;
                    constructorImplementingClass = this.getClass().getConstructor(new Class[] { List.class });
                    return (RequestHeaders) constructorImplementingClass.newInstance(new Object[] { possibleSubHeaders });
                } catch (Exception e) {
                    // do not react to any of many possible exceptions
                    // sorry, debug or add logging if this you get not subheaders out here.
                }
            }
            // test if subheader are given as RequestHeaders
            else if (possibleSubHeaders instanceof RequestHeaders) {
                return (RequestHeaders) possibleSubHeaders;
            }
        }
        // no headers found if returning from here
        return null;
    }

    //
    // Add getters to common header types here.
    //
    // These getters have to be overwritten in protocol specific classes to work.
    //

    /**
     * Returns the <i>ReplyTo-Address</i> especially used in asynchronous communications.
     *
     * @return address to reply to
     */
    public String getReplyToAddress() {
        return null;
    }
}
