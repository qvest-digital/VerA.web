package de.tarent.aa.veraweb.worker;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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

    public static final String[] INPUT_showDetail = {PARAM_LOCATION_ID};
    public static final boolean[] MANDATORY_showDetail = {false};
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


	public Location showDetail(OctopusContext context, Integer id) throws BeanException, IOException{
	    if(id == null){
	        return null;
	    }
	    Location location = getLocation(context, id);

	    return location;
	}


	static public Location getLocation(OctopusContext context, Integer id) throws BeanException, IOException {

	    if(id == null) {
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

            Location oldlocation = (Location) database.getBean("Location", location.getId(),transactionContext);

            if(location.id == null || location.compareTo(oldlocation)!=0) {
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
