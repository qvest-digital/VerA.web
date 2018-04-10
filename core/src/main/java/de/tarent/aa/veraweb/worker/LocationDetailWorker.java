package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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

import de.tarent.aa.veraweb.beans.Location;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.BeanChangeLogger;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWebFactory;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;

/**
 * Dieser Octopus-Worker liefert eine Detailansicht für Veranstaltungsorte
 */
public class LocationDetailWorker {

    private static final String PARAM_LOCATION = "location";
    private static final String PARAM_LOCATION_ID = "id";

    public static final String[] INPUT_showDetail = { PARAM_LOCATION_ID };
    public static final boolean[] MANDATORY_showDetail = { false };
    public static final String OUTPUT_showDetail = PARAM_LOCATION;

    private final DatabaseVeraWebFactory databaseVeraWebFactory;

    //
    // Konstruktoren
    //

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */

    public LocationDetailWorker() {
        this(new DatabaseVeraWebFactory());
    }

    public LocationDetailWorker(DatabaseVeraWebFactory databaseVeraWebFactory) {
        this.databaseVeraWebFactory = databaseVeraWebFactory;
    }

    public Location showDetail(OctopusContext context, Integer id) throws BeanException, IOException {
        if (id == null) {
            return null;
        }
        Location location = getLocation(context, id);

        return location;
    }

    static public Location getLocation(OctopusContext context, Integer id) throws BeanException, IOException {

        if (id == null) {
            return null;
        }

        Database database = new DatabaseVeraWeb(context);
        Location location = (Location) database.getBean("Location", id);

        return location;
    }

    public static final String INPUT_saveDetail[] = { "savelocation" };
    public static final boolean MANDATORY_saveDetail[] = { false };

    public void saveDetail(final OctopusContext octopusContext, Boolean savelocation)
            throws BeanException, IOException {
        if (savelocation == null || !savelocation.booleanValue()) {
            return;
        }

        Request request = new RequestVeraWeb(octopusContext);

        Database database = databaseVeraWebFactory.createDatabaseVeraWeb(octopusContext);
        TransactionContext transactionContext = database.getTransactionContext();

        try {
            Location location = (Location) octopusContext.contentAsObject(PARAM_LOCATION);
            if (location == null) {
                location = (Location) request.getBean("Location", PARAM_LOCATION);
            }

            location.verify(octopusContext);

            Location oldlocation = (Location) database.getBean("Location", location.getId(), transactionContext);

            if (location.id == null || location.compareTo(oldlocation) != 0) {
                location.setModified(true);
            }

            if (location.isModified() && location.isCorrect()) {
                BeanChangeLogger clogger = new BeanChangeLogger(database,
                        transactionContext);
                if (location.getId() == null) {
                    octopusContext.setContent("countInsert", Integer.valueOf(1));
                    database.getNextPk(location, transactionContext);

                    location.setOrgunit(((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId());

                    Insert insert = database.getInsert(location);
                    insert.insert("pk", location.getId());

                    transactionContext.execute(insert);
                    transactionContext.commit();

                    clogger.logInsert(octopusContext.personalConfig().getLoginname(), location);
                } else {
                    octopusContext.setContent("countUpdate", Integer.valueOf(1));
                    Update update = database.getUpdate(location);
                    transactionContext.execute(update);
                    transactionContext.commit();
                }
            } else {
                octopusContext.setStatus("notsaved");
            }
            octopusContext.setContent(PARAM_LOCATION, location);

            transactionContext.commit();
        } catch (BeanException e) {
            transactionContext.rollBack();
            // must report error to user
            throw new BeanException(
                    "Die location details konnten nicht gespeichert werden.", e);
        }
    }
}
