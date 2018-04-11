package de.tarent.commons.datahandling;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Collection;

/**
 * Abstraction for a list of Integer Primary Keys.
 * This List has a simple String representation, which is provided
 * by the String constructor and the toString() method.
 * The String representation ist a comma "," separated list.
 */
public class PrimaryKeyList extends LinkedList {

    public static final String DELIM = ",";

    public PrimaryKeyList() {
        super();
    }

    /**
     * Lnitializes the list with a String list.
     */
    public PrimaryKeyList(String listRepresentation) {
        super();
        StringTokenizer st = new StringTokenizer(listRepresentation, DELIM);
        while (st.hasMoreTokens()) {
            add(new Integer(st.nextToken()));
        }
    }

    /**
     * Returns a PrimaryKeyList representations for the supplied collection.
     * If the collection is already a PrimaryKeyList instance, this is simply a cast.
     */
    public static PrimaryKeyList getPrimaryKeysListFor(Collection c) {
        if (c instanceof PrimaryKeyList) {
            return (PrimaryKeyList) c;
        }

        PrimaryKeyList pkList = new PrimaryKeyList();
        pkList.addAll(c);
        return pkList;
    }

    public String toString() {
        return toString(DELIM, this);
    }

    public static String toString(String delim, Collection collection) {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = collection.iterator(); iter.hasNext(); ) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }
}