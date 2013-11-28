package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Functions needed by webapp to handle event tasks.
 */
public class EventTaskListWorker extends ListWorkerVeraWeb {
	
    
	
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
    
    @Override
    public List<Task> showList(OctopusContext cntx) throws IOException, BeanException {
        List<Task> list = super.showList(cntx);

        cntx.setContent("id", cntx.requestAsString("id"));
        
        Database database = new DatabaseVeraWeb(cntx);
        
        for (Task task : list) {
            Person person = (Person) database.getBean("Person", task.getPersonId());
            // Select statement bauen um  person mit 'personId' aus der db zu holen
            
            if(person != null){
                task.setPersonName(person.lastname_a_e1 + ", " + person.firstname_a_e1);
            }
        }
        
        return list;
    }  
    
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        WhereList where = new WhereList();
        String strEventId = cntx.getRequestObject().getParamAsString("id");
        Integer eventId = Integer.valueOf(strEventId);
        where.addAnd(Expr.equal("fk_event", eventId));
        select.where(where);
    }
    
    /**
     * Bestimmt ob eine Aufgabe aufgrund bestimmter Kriterien gelöscht wird oder nicht.
     */
    @Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
       
        int count = 0;
        if (selection == null || selection.size() == 0) {
        	return count;
        }
        Database database = context.getDatabase();

        Task task = (Task)database.createBean("Task");
        Clause clause = Expr.in("pk", selection);
        Select select = database.getSelectIds(task).where(clause);

        if(!selection.isEmpty()) {
            try
            {
            
                Map data;
                for (Iterator it = database.getList(select, context).iterator(); it.hasNext(); ) {
                    data = (Map)it.next();
                    task.id = (Integer)data.get("id");
                    if (removeBean(cntx, task, context)) {
                        selection.remove(task.id);
                        count++;
                    }
                }
                context.commit();
            }
            catch ( BeanException e )
            {
                context.rollBack();
                throw new BeanException( "Die Aufgabe(n) konnten nicht gelöscht werden.", e );
            }
        }
        return count; 
    }
}
