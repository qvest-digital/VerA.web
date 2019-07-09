package de.tarent.aa.veraweb.worker;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.util.List;

/**
 * Worker der eine Autovervollständigung verschiedener Stammdaten zur
 * Verfügung stellt.
 *
 * @author Christoph
 */
public class CompleteWorker {
    private static final Limit LIMIT = new Limit(new Integer(10), new Integer(0));
    private static final String COLUMN = "entry";
    private static final String QUERY = "query";
    private static final String RESULT = "list";

    /**
     * @param cntx   {@link OctopusContext}
     * @param table  FIXME
     * @param column Column
     * @param query  Query
     * @return Liste mit den 10 ersten Einträgen.
     * @throws BeanException BeanException
     */
    private List getList(OctopusContext cntx, String table, String column, String query) throws BeanException {
        cntx.setContent(QUERY, query);
        Database database = new DatabaseVeraWeb(cntx);
        return database.getList(SQL.Select(database).
          from(table).
          selectAs(column, COLUMN).
          where(Expr.like(column, query + '%')).
          orderBy(Order.asc(column)).
          Limit(LIMIT), database);
    }

    /**
     * Octopus-Eingabeparameter der Aktion {@link #completeLocation(OctopusContext, String)}
     */
    public static final String INPUT_completeLocation[] = { QUERY };
    /**
     * Octopus-Ausgabeparameter der Aktion {@link #completeLocation(OctopusContext, String)}
     */
    public static final String OUTPUT_completeLocation = RESULT;

    /**
     * @param cntx  Octopus-Context
     * @param query Aktuelle Benutzereingabe
     * @return Liste mit ähnlichen Locations.
     * @throws BeanException beanexception
     */
    public List completeLocation(OctopusContext cntx, String query) throws BeanException {
        return getList(cntx, "veraweb.tlocation", "locationname", query);
    }
}
