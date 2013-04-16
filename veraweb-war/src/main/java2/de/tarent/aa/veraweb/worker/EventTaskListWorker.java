package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Task;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
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
    
    public List<Task> showList(OctopusContext cntx) throws IOException, BeanException {
        List<Task> list = super.showList(cntx);

        
        Database database = new DatabaseVeraWeb(cntx);
        
        for (Task task : list) {
            Person person = (Person) database.getBean("Person", task.getPersonId());
            // Select statement bauen um  person mit 'personId' aus der db zu holen
            
            task.setPersonName(person.lastname_a_e1 + ", " + person.firstname_a_e1);
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
}
