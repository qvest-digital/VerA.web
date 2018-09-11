package de.tarent.dblayer.sql;

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

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * Named parameter for insert into statements.
 */
public class ParamValue extends SetDbContextImpl implements Cloneable {
    String name;
    Object value;
    boolean set = false;
    boolean optional = false;

    public ParamValue(String parameterName) {
        this.name = parameterName;
    }

    public ParamValue(String parameterName, boolean optional) {
        this.name = parameterName;
        this.optional = optional;
    }

    public ParamValue(String parameterName, Object parameterValue) {
        this.name = parameterName;
        setValue(parameterValue);
    }

    /**
     * Returns the parameters value, if set. The value even may be <code>null</code>.
     * If the value was not set, this method throws an IllegalArgumentException.
     *
     * @throws IllegalArgumentException if no value was set
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the parameters value. The value even may be <code>null</code>.
     */
    public void setValue(Object newValue) {
        this.value = newValue;
        set = true;
    }

    /**
     * Returns true, if the value was set, false otherwise
     */
    public final boolean isSet() {
        return set;
    }

    /**
     * Returns, if the ParamValue should be treated as an optional Parameter
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Clears the Parameter value
     */
    public void clear() {
        value = null;
        set = false;
    }

    /**
     * @return the parameters name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the formated value, if this parameter was set.
     * Otherwise the SQL param symbol '?' will be returned for usage in an prepared statement.
     * This method relies on an previous set DBContext. It no context was set, it will fall
     * back to the {#see {@link SQL#format(DBContext, Object)} default behavior.
     */
    public String toString() {
        if (!isSet()) {
            return "?";
        }

        return SQL.format(getDBContext(), getValue());
    }

    /**
     * Returns an independent clone of this statement.
     * ATTENTION: The value element of the expression will no be copied
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            ParamValue theClone = (ParamValue) super.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
