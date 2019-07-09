package de.tarent.dblayer.sql;

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

import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.SetDbContextImpl;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.RawClause;

/**
 * This class represents a <code>JOIN</code> part of a <code>SELECT</code>
 * statement.
 */
public class Join extends SetDbContextImpl {
    //
    // public constants
    //
    /**
     * join type: inner join
     */
    public static final int INNER = 1;
    /**
     * join type: left outer join
     */
    public static final int LEFT_OUTER = 2;
    /**
     * join type: right outer join
     */
    public static final int RIGHT_OUTER = 3;
    /**
     * join type: outer join
     */
    public static final int OUTER = 4;

    //
    // constructors
    //

    /**
     * This constructor creates a join using a custom join {@link Clause}.<br>
     * It sets the <code>joinHead</code> member to the required joining keywords
     * and the table name and saves the given clause for {@link DBContext}
     * sensitive serialization just in time.
     *
     * @param type   type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *               and {@link #RIGHT_OUTER}.
     * @param table  name of the table to join
     * @param clause the join {@link Clause}; <code>null</code> for cross joins.
     */
    public Join(int type, String table, Clause clause) {
        StringBuffer buffer = new StringBuffer(20);
        appendJoin(buffer, type);
        buffer.append(table).append(' ');
        joinHead = buffer.toString();
        this.clause = clause;
    }

    /**
     * This constructor creates a cross join.
     *
     * @param type  type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *              and {@link #RIGHT_OUTER}.
     * @param table name of the table to join
     */
    public Join(int type, String table) {
        this(type, table, null);
    }

    /**
     * This constructor creates a join on the two given columns.
     *
     * @param type        type of the join, one of {@link #INNER}, {@link #LEFT_OUTER}
     *                    and {@link #RIGHT_OUTER}.
     * @param table       name of the table to join
     * @param leftColumn  left side column name of the equality
     * @param rightColumn right side column name of the equality
     */
    public Join(int type, String table, String leftColumn, String rightColumn) {
        this(type, table, new RawClause(leftColumn + '=' + rightColumn));
    }

    //
    // class {@link Object}
    //

    /**
     * This method returns a {@link String} representation of the join. It is a
     * concatenation of the <code>joinHead</code> and a String representation of
     * the join {@link Clause} created using the current {@link DBContext}.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (clause == null) {
            return joinHead;
        }
        StringBuffer buffer = new StringBuffer(joinHead);
        buffer.append("ON (")
          .append(clause.clauseToString(getDBContext()))
          .append(") ");
        return buffer.toString();
    }

    //
    // protected helper methods
    //

    /**
     * This method appends the join keywords to the given {@link StringBuffer}
     * according to the given type.
     */
    protected static void appendJoin(StringBuffer buffer, int type) {
        switch (type) {
        case LEFT_OUTER:
            buffer.append(LEFT_OUTER_JOIN);
            break;
        case RIGHT_OUTER:
            buffer.append(RIGHT_OUTER_JOIN);
            break;
        case OUTER:
            buffer.append(OUTER_JOIN);
            break;
        default:
            buffer.append(SIMPLE_JOIN);
            break;
        }
    }

    //
    // private constants
    //
    /**
     * the String "<code> JOIN </code>"
     */
    private static final String SIMPLE_JOIN = " JOIN ";
    /**
     * the String "<code> LEFT OUTER JOIN </code>"
     */
    private static final String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    /**
     * the String "<code> RIGHT OUTER JOIN </code>"
     */
    private static final String RIGHT_OUTER_JOIN = " RIGHT OUTER JOIN ";
    /**
     * the String "<code> FULL JOIN </code>"
     */
    private static final String OUTER_JOIN = " FULL JOIN ";

    //
    // protected member variables
    //
    /**
     * this is the concatenation of the appropriate join keywords, the table name and a ' '
     */
    private final String joinHead;
    /**
     * this is the join {@link Clause}; it may be <code>null</code> for cross joins
     */
    private final Clause clause;
}
