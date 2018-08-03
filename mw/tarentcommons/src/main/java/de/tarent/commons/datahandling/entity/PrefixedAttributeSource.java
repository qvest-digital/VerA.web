package de.tarent.commons.datahandling.entity;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Implementation for the AttributeSource, which delegates to another attribute source using a prefix for each requested
 * property name.
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public class PrefixedAttributeSource implements AttributeSource {
    String prefix;
    AttributeSource delegate;
    private List attributeNames;

    public PrefixedAttributeSource(String prefix, AttributeSource delegate) {
        setPrefix(prefix);
        setDelegate(delegate);
    }

    public Object getAttribute(String attributeName) {
        return delegate.getAttribute(prefix.concat(attributeName));
    }

    public Class getAttributeType(String attributeName) {
        Object param = delegate.getAttribute(prefix.concat(attributeName));
        return param == null ? null : param.getClass();
    }

    /**
     * Returns a list the attribute names
     *
     * @return list of Strings
     */
    public List getAttributeNames() {
        if (attributeNames == null) {
            attributeNames = new ArrayList();
            for (Iterator iter = delegate.getAttributeNames().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                if (name.startsWith(prefix)) {
                    attributeNames.add(name.substring(prefix.length()));
                }
            }
        }
        return attributeNames;
    }

    /**
     * Helper method to test, if any of the values in this AS are != null
     */
    public boolean hasNotNullFields() {
        List attribs = getAttributeNames();
        for (Iterator iter = attribs.iterator(); iter.hasNext(); ) {
            if (getAttribute((String) iter.next()) != null) {
                return true;
            }
        }
        return false;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String newPrefix) {
        this.prefix = newPrefix;
    }

    public AttributeSource getDelegate() {
        return delegate;
    }

    public void setDelegate(AttributeSource newDelegate) {
        this.delegate = newDelegate;
    }
}
