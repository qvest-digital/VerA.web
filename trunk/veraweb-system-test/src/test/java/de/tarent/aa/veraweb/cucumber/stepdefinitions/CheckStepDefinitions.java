package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Dann;
import cucumber.annotation.de.Und;
import cucumber.table.DataTable;
import de.tarent.aa.veraweb.cucumber.env.EntityMapping;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.db.dao.PersonDao;
import de.tarent.aa.veraweb.db.dao.TaskDao;
import de.tarent.aa.veraweb.db.entity.Person;
import de.tarent.aa.veraweb.db.entity.Task;

public class CheckStepDefinitions extends AbstractStepDefinitions {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ObjectFinder objectFinder;

    @Dann("^bin ich erfolgreich vom System abgemeldet$")
    public void thenSuccessfullyLoggedOut() {
        thenAtPage(PageDefinition.ABMELDUNGSSEITE);
    }

    @Dann("^sehe ich als \"([^\"]+)\" die Meldung \"([^\"]+)\"$")
    @Und("^ich sehe als \"([^\"]+)\" die Meldung \"([^\"]+)\"$")
    public void thenElementTextEquals(String elementName, String expectedText) {
        thenElementTextEquals(page.elementForName(elementName), expectedText);
    }

    @Dann("^sehe ich eine Tabelle mit folgenenden Aufgaben:$")
    public void thenTableWithTasksIsPresent(DataTable data) throws Exception {
        List<Task> tasks = EntityMapping.createEntities(data, Task.class);
        for (Task task : tasks) {
            if (task.getResponsiblePerson() != null && task.getResponsiblePerson().getFirstName() != null) {
                Person personDB = personDao.getPersonByFirstname(task.getResponsiblePerson().getFirstName());
                task.setResponsiblePerson(personDB);
            }
        }
        objectFinder.checkForObjects(tasks);
    }

    @Dann("^sehe ich eine Rückfrage zur Bestätigung des Löschens einer ([^\"]+)$")
    public void thenConfirmationDialogIsPresent(String name) throws Exception {
        Alert alert = driver.switchTo().alert();
        assertEquals(alert.getText(), "Markierte " + name + "(n) wirklich entfernen?");
        alert.dismiss();
    }

    @Dann("wird die Aufgabe \"([^\"]+)\" gelöscht")
    public void thenTaskIsDeleted(String taskTitle) throws Exception {
        Task task = taskDao.getTask(taskTitle);
        assertNull("Task should be deleted!", task);
    }
}
