package de.tarent.dblayer.sql.clause;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;

/**
 * This class represents a SQL function consisting of a name and optionally
 * a number of parameters.
 */
public class Function extends SetDbContextImpl {
    //
    // protected member variables
    //
    /**
     * name of the function
     */
    String name;
    /**
     * list of parameter description of the function
     */
    ArrayList parameter = new ArrayList();

    //
    // constructors
    //

    /**
     * This constructor accepts only the name of the function.
     *
     * @deprecated use {@link #Function(DBContext, String)} instead
     */
    public Function(String name) {
        this.name = name;
    }

    /**
     * This constructor accepts the name of the function and the
     * {@link DBContext} its literal parameters are to be formatted
     * according to.
     */
    public Function(DBContext dbc, String name) {
        this.name = name;
        setDBContext(dbc);
    }

    /**
     * This constructor accepts the name of the function, one column
     * parameter and a number of literal parameters.
     */
    public Function(String name, String column, Collection param) {
        this.name = name;
        column(column);
        parameter(param);
    }

    /**
     * This constructor accepts the name of the function, a number of
     * literal parameters and one column parameter.
     */
    public Function(String name, Collection param, String column) {
        this.name = name;
        parameter(param);
        column(column);
    }

    /**
     * This constructor accepts the name of the function and a number
     * of literal parameters
     */
    public Function(String name, Collection param) {
        this.name = name;
        parameter(param);
    }

    //
    // public method
    //

    /**
     * This method adds a number of literal parameters.
     */
    public Function parameter(Collection param) {
        for (Iterator it = param.iterator(); it.hasNext(); ) {
            parameter.add(new LiteralWrapper(it.next()));
        }
        return this;
    }

    /**
     * This method adds a literal parameter.
     */
    public Function parameter(Object param) {
        parameter.add(new LiteralWrapper(param));
        return this;
    }

    /**
     * This method adds a column parameter.
     */
    public Function column(String column) {
        parameter.add(column);
        return this;
    }

    //
    // class {@link Object}
    //

    /**
     * This method returns a serialization of this {@link Function}
     * according to the current {@link DBContext}.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(name);
        buffer.append("(");
        for (Iterator it = parameter.iterator(); it.hasNext(); ) {
            buffer.append(it.next());
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * Returns an independent clone of this statement.
     *
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Function theClone = (Function) super.clone();
            theClone.parameter = (ArrayList) parameter.clone();
            return theClone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
