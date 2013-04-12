package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Task;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskListWorker extends ListWorkerVeraWeb {
	
    public static final String[] INPUT_getTasks = {"id"};
	//public static final boolean[] MANDATORY_getTasks = {true};
	public static final String OUTPUT_getTasks = "tasks";
	
	/**
	 * Load and return all tasks of the event with the given id.
	 * 
	 * @param oc
	 * @param eventId
	 * @return
	 */
	
    //
    // Konstruktoren
    //
    /** Default-Kontruktor der den Beannamen festlegt. */
    public EventTaskListWorker() {
        super("Task");
    }
    
    public Task getTasks(OctopusContext cntx, String id) throws BeanException {
        Task search = null;
        search = new Task();
        cntx.setContent("id", id);
        Integer eventId = null;
        try {
            eventId = Integer.valueOf(id);
        } catch (Exception e) {

        }
        search.setEventId(eventId);
        return search;
    }
    
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        WhereList where = new WhereList();
        String strEventId = cntx.contentAsString("id");
        Integer eventId = Integer.valueOf(strEventId);
        where.addAnd(Expr.equal("fk_event", eventId));
        select.where(where);
    }
}
