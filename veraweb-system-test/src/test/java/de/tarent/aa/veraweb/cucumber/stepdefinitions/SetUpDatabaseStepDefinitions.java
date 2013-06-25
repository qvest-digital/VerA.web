package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Angenommen;
import cucumber.table.DataTable;
import de.tarent.aa.veraweb.cucumber.env.EntityMapping;
import de.tarent.aa.veraweb.db.dao.EventDao;
import de.tarent.aa.veraweb.db.dao.OrgunitDao;
import de.tarent.aa.veraweb.db.dao.PersonDao;
import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Event;
import de.tarent.aa.veraweb.db.entity.Orgunit;
import de.tarent.aa.veraweb.db.entity.Person;
import de.tarent.aa.veraweb.db.entity.Task;

/**
 * Step Definitions for presetting database with proper test data.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 */
public class SetUpDatabaseStepDefinitions {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private OrgunitDao orgunitDao;

    /**
     * Set up database with expected {@link Person}s.
     * 
     * @param dt
     *            data table containing model's properties
     * @throws Exception
     *             exception
     */
    @Angenommen("^es existieren die Personen:$")
    public void existPersons(DataTable dt) throws Exception {
        List<Person> persons = EntityMapping.createEntities(dt, Person.class);

        for (Person person : persons) {
            if (person.getOrgunit() == null) {
                person.setOrgunit(orgunitDao.find(Orgunit.DEFAULT_ORGUNIT_ID));
            }
            personDao.persist(person);
        }
    }

    /**
     * Set up database with expected {@link Person}s.
     * 
     * @param dt
     *            data table containing model's properties
     * @throws Exception
     *             exception
     */
    @Angenommen("^es existiert eine Veranstaltung \"([^\"]+)\" mit folgenden Aufgaben:$")
    public void existEventWithRelatedTasks(String evenShortName, DataTable dt) throws Exception {
        /*
         *  create and persist new event
         */
        Event event = new Event();
        event.setShortName(evenShortName);
        if (event.getOrgunit() == null) {
            event.setOrgunit(orgunitDao.find(Orgunit.DEFAULT_ORGUNIT_ID));
        }
        Event.fillEmptyMandatoryFields(event);
        eventDao.persist(event);

        /*
         *  create new tasks related to the event above
         */
        List<Task> tasks = EntityMapping.createEntities(dt, Task.class);

        for (Task task : tasks) {
            task.setId(null);
            task.setEvent(event);

            if (task.getResponsiblePerson() != null && task.getResponsiblePerson().getFirstName() != null) {
                Person personDB = personDao.getPersonByFirstname(task.getResponsiblePerson().getFirstName());
                task.setResponsiblePerson(personDB);
            }

            taskDao.persist(task);
        }
    }

}
