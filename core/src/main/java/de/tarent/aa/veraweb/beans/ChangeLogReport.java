package de.tarent.aa.veraweb.beans;
import java.sql.Date;
import java.util.Calendar;

/**
 * TODO refactor existing change log bean management to this class
 *
 * The bean class ChangeLogReport represents a
 * session bean that models the configuration
 * for a change log report.
 *
 * As of now, the change log reports can be
 * filtered by their begin and end date, with
 * their respective defaults being set to
 * begin ::= 1st January of the current year
 * and end ::= NOW, respectively.
 *
 * @author cklein
 * @see de.tarent.aa.veraweb.worker.ChangeLogReportsWorker
 * @since 1.2.0
 */
public class ChangeLogReport extends AbstractBean {
    public Date begin;
    public Date end;

    /**
     * Constructs a new instance of this.
     */
    public ChangeLogReport() {
        this.begin = Date.valueOf("01.01." + Calendar.getInstance().get(Calendar.YEAR));
        this.end = new Date(System.currentTimeMillis());
    }
}
