package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class OnlineRegistrationFunctionWorker extends ListWorkerVeraWeb {

    public static final String INPUT_showAllFunctions[] = {};

    private final static Logger LOGGER = Logger.getLogger(OnlineRegistrationFunctionWorker.class.getCanonicalName());

    public OnlineRegistrationFunctionWorker() {
        super("EventFunction");
    }

    /**
     * Show all available functions.
     *
     * @param octopusContext The {@link OctopusContext}
     */
    public void showAllFunctions(OctopusContext octopusContext) {
        final Database database = new DatabaseVeraWeb(octopusContext);

        final List functions = getEventDoctypeList(database);

        octopusContext.setContent("allFunctions", functions);
    }

    private static List getEventDoctypeList(Database database) {
        Select select = null;
        try {
            select = database.getSelect("Function");
        } catch (BeanException e) {
            LOGGER.error("Fehler bei der Abfrage nach der verfügbaren Funktionen/Amtsbezeichnungen");
        } catch (IOException e) {
            LOGGER.error("IO-Fehler bei der Abfrage nach der verfügbaren Funktionen/Amtsbezeichnungen");
        }

        try {
            return database.getBeanList("Function", select);
        } catch (BeanException e) {
            LOGGER.error("Fehler bei der Verarbeitung der verfügbaren Funktionen/Amtsbezeichnungen");
        }
        return null;
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        select.where(Expr.equal("fk_event", getEvent(cntx).id));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException {
        select.join("veraweb.tfunction", "tevent_function.fk_function", "tfunction.pk");
        select.selectAs("tfunction.functionname", "name");
        select.orderBy(Order.asc("name"));
    }

    private Event getEvent(OctopusContext cntx) {
        return (Event)cntx.contentAsObject("event");
    }
}
