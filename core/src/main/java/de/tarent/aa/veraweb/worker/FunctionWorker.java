package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Function;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.List;

/**
 * Diese Liste zeigt eine Liste von Funktionen an.
 *
 * @author Christoph
 * @version $Revision: 1.1 $
 */
public class FunctionWorker extends StammdatenWorker {
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public FunctionWorker() {
        super("Function");
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        Event event = (Event) cntx.contentAsObject("event");
        if (event != null) {
            select.where(getClause(event));
        }
    }

    private RawClause getClause(Event event) {
        String clause = "pk NOT IN (SELECT fk_function FROM veraweb.tevent_function WHERE fk_event = " + event.id + ")";
        return new RawClause(clause);
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
        final Function function = (Function) bean;
        if (!function.equals("")) {
            super.saveBean(cntx, bean, context);
        } else {
            return;
        }
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
        super.saveList(octopusContext);
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        return super.showList(octopusContext);
    }
}
