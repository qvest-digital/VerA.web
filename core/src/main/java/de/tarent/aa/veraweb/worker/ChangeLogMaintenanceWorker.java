package de.tarent.aa.veraweb.worker;

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
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
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

import de.tarent.aa.veraweb.beans.Duration;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Worker that runs constantly purges old entries from the changelog.
 *
 * @author cklein
 * @version $Revision: 1.1 $
 * @see ChangeLogMaintenanceWorker
 */
public class ChangeLogMaintenanceWorker implements Runnable {
    /**
     * Log4J Logger Instanz
     */
    private final Logger logger = LogManager.getLogger(ChangeLogMaintenanceWorker.class);
    /**
     * Actual worker thread
     */
    protected Thread thread;
    protected boolean keeprunning = false;
    protected boolean isworking = false;
    /**
     * Gibt die Wartezeit zwischen zwei Dispatch aufrufen an.
     */
    protected int waitMillis = 0;
    protected OctopusContext cntx = null;

    protected Duration retentionPolicy;

    /**
     * Octopus-Eingabe-Parameter für {@link #load(OctopusContext)}
     */
    public static final String INPUT_load[] = {};

    private Duration getConfiguration() {
        Duration result = Duration.fromString("P0");

        List list = (List) cntx.contentAsObject("allConfig");
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map entry = (Map) it.next();
            if ("changeLogRetentionPolicy".compareTo((String) entry.get("key")) == 0) {
                result = Duration.fromString((String) entry.get("value"));
                break;
            }
        }

        return result;
    }

    /**
     * Starts the background maintenance service.
     *
     * @param cntx Octopus-Context
     */
    public void load(OctopusContext cntx) {
        this.logger.info("ChangeLogMaintenanceWorker wird im Hintergrund gestartet.");
        this.cntx = cntx;

        this.retentionPolicy = this.getConfiguration();
        if (this.retentionPolicy.toString().equals("P0")) {
            // log invalid setting and use 1yr. default
            this.logger
              .warn("changeLogRetentionPolicy Konfigurationseinstellung ist fehlerhaft. Die Einstellung muss einer " +
                "g\u00fcltigen " +
                "Zeitdauerangabe im Format P[0-9]+Y[0-9]+M[0-9]+D entsprechen. Stattdessen wird die Vorgabe P1Y (1 " +
                "Jahr) verwendet.");
            this.retentionPolicy.years = 1;
        }

        // this setting is unconfigurable, by default, the maintenance routine
        // will be run once every two hours
        this.waitMillis = 7200000;

        // Server status
        if (!keeprunning) {
            this.keeprunning = true;
            this.thread = new Thread(this);
            this.thread.start();
        } else {
            this.unload();
        }
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #unload()}
     */
    public static final String INPUT_unload[] = {};

    /**
     * Stops the background service.
     */
    public void unload() {
        this.logger.info("ChangeLogMaintenanceWorker wird gestoppt.");
        this.keeprunning = false;
    }

    /**
     * @see Runnable#run()
     */
    public void run() {
        try {
            this.isworking = true;
            while (this.keeprunning) {
                try {
                    this.purgeChangeLog();
                } catch (Exception e) {
                    this.logger.error("ChangeLogMaintenanceWorker: allgemeiner Fehler w\u00e4hrend der Wartungsarbeit.", e);
                }
                try {
                    Thread.sleep(this.waitMillis < 1000 ? 1000 : this.waitMillis);
                } catch (InterruptedException e) {
                    // just catch
                }
            }
        } finally {
            this.isworking = false;
            this.thread = null;
        }
    }

    /**
     * Purges the change log from old information.
     *
     * @throws IOException   ioException
     * @throws BeanException beanException
     * @throws SQLException  sqlException
     */
    public void purgeChangeLog() throws SQLException, BeanException, IOException {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.YEAR, -1 * this.retentionPolicy.years);
        c.add(Calendar.MONTH, -1 * this.retentionPolicy.months);
        c.add(Calendar.DAY_OF_MONTH, -1 * this.retentionPolicy.days);
        Date d = new Date(c.getTimeInMillis());

        TransactionContext transactionContext = (new DatabaseVeraWeb(this.cntx)).getTransactionContext();
        try {
            Delete delete = transactionContext.getDatabase().getDelete("ChangeLogEntry");
            delete.where(Expr.lessOrEqual("date", d.toString()));
            transactionContext.execute(delete);
            transactionContext.commit();
        } catch (Throwable e) {
            transactionContext.rollBack();
            logger.trace("Das Changelog konnte nicht gel\u00f6scht werden.", e);
        }
    }
}
