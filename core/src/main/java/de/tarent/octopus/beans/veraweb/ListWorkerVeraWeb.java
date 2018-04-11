package de.tarent.octopus.beans.veraweb;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */

import de.tarent.aa.veraweb.worker.JumpOffset;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.GroupBy;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanListWorker;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse konkretisiert den abstrakten Basis-Worker {@link BeanListWorker}
 * auf die Benutzung der VerA.web-spezifischen Implementierungen von
 * {@link Database} und {@link Request} hin.
 */
public abstract class ListWorkerVeraWeb extends BeanListWorker {
    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor gibt den Namen der zugrunde liegenden Bean weiter.
     */
    protected ListWorkerVeraWeb(String beanName) {
        super(beanName);
    }

    //
    // Oberklasse BeanListWorker
    //

    /**
     * Diese Methode liefert eine {@link DatabaseVeraWeb}-Instanz
     *
     * @see BeanListWorker#getDatabase(OctopusContext)
     */
    @Override
    protected Database getDatabase(OctopusContext cntx) {
        return new DatabaseVeraWeb(cntx);
    }

    /**
     * Diese Methode liefert eine {@link RequestVeraWeb}-Instanz.
     *
     * @see BeanListWorker#getRequest(OctopusContext)
     */
    @Override
    protected Request getRequest(OctopusContext cntx) {
        return new RequestVeraWeb(cntx);
    }

    /**
     * Table column to use for Jump Offsets (a.k.a. "Direkteinsprung")
     * <br>
     *
     * @param octopusContext The {@link OctopusContext}
     * @return the db column name or <code>null</code> if no jump offsets should be generated.
     * @throws BeanException beanException
     */
    protected String getJumpOffsetsColumn(OctopusContext octopusContext) throws BeanException {
        return null;
    }

    public static final String INPUT_getJumpOffsets[] = {};
    public static final String OUTPUT_getJumpOffsets = "jumpOffsets";

    public List<JumpOffset> getJumpOffsets(OctopusContext octopusContext) throws BeanException, IOException, SQLException {
        final Database database = getDatabase(octopusContext);
        final Integer start = getStart(octopusContext);
        final Integer limit = getLimit(octopusContext);
        final String col = getJumpOffsetsColumn(octopusContext);
        if (col == null) {
            return null;
        }
        final Select subQuery = getSelect(database);
        extendWhere(octopusContext, subQuery);
        extendColumns(octopusContext, subQuery);
        subQuery.setDistinct(false);

        final Select statement = SQL
          .Select(database)
          .setDistinct(false)
          .selectAs("s1.letter", "letter")
          .selectAs("min(rownum)", "rownum")
          .from("( select upper(substring(trim(" + col + "),1,1)) as letter, row_number() over () -1 as rownum from (" +
            subQuery.statementToString() + " ) s0 ) ", "s1")
          .groupBy(new GroupBy("letter"))
          .orderBy(Order.asc("rownum"));
        ResultSet rs = statement
          .getResultSet();
        final ArrayList<JumpOffset> offsets = new ArrayList<JumpOffset>();
        while (rs.next()) {
            offsets.add(new JumpOffset(rs.getString("letter"), rs.getInt("rownum"), start, limit));
        }
        return offsets;
    }
}
