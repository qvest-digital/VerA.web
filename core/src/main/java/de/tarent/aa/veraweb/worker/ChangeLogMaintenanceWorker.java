package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Duration;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

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
@Log4j2
public class ChangeLogMaintenanceWorker implements Runnable {
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
        Duration result = Duration.fromString("P1Y");

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
