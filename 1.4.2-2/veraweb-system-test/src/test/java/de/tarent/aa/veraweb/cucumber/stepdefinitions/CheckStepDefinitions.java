package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Angenommen;
import cucumber.annotation.de.Dann;
import cucumber.table.DataTable;
import de.tarent.aa.veraweb.cucumber.env.EntityMapping;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.db.dao.GenericDao;
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

    @Dann("^(?:sehe ich|ich sehe) als \"([^\"]+)\" die Meldung \"([^\"]+)\"$")
    public void thenSpecifiedInformationTextEquals(String elementName, String expectedText) {
        thenElementTextEquals(page.elementForName(elementName), expectedText);
    }
    
    @Angenommen("^ich sehe als \"([^\"]+)\" den Text \"([^\"]+)\"$")
    @Dann("^sehe ich als \"([^\"]+)\" den Text \"([^\"]+)\"$")
    public void thenSpecifiedNaviListTextEquals(String elementName, String expectedText) {
        thenElementTextEquals(page.elementForName(elementName), expectedText);
    }

    @Dann("^(?:sehe ich|ich sehe) die Meldung \"([^\"]+)\"$")
    public void thenInformationTextEquals(String expectedText) {
        thenElementTextEquals(page.elementForName("Infobox"), expectedText);
    }

    @Dann("^(?:sehe ich|ich sehe) eine Tabelle mit folgenenden Aufgaben:$")
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

    @Dann("(?:ich sehe|sehe ich) eine leere Tabelle")
    public void thenEmptyTableIsPresent() {
        List<WebElement> rows = driver.findElements(By.xpath("//table[@class='list']/tbody/tr"));
        assertEquals(1, rows.size()); // only table header is displayed
    }

    @Dann("^sehe ich eine Rückfrage zur Bestätigung des Löschens einer ([^\"]+)$")
    public void thenConfirmationDialogIsPresent(String name) throws Exception {
        Alert alert = driver.switchTo().alert();
        assertEquals(alert.getText(), "Markierte " + name + "(n) wirklich entfernen?");
        alert.dismiss();
    }

    @Dann("wird die ([^\"]+) mit der ID ([^\" ]+) (nicht |)gelöscht$")
    public void thenEntityIsDeleted(String elementName, Long id, String negation) throws Exception {
        GenericDao<?, Long> dao = null;

        if ("Aufgabe".equals(elementName)) {
            dao = taskDao;
        } else {
            fail("Element name '" + elementName + "' is undefined!");
        }

        if (negation == null || negation.trim().isEmpty()) {
            assertNull(elementName + " should be deleted!", dao.find(id));
        } else {
            assertNotNull(elementName + " must not be deleted!", dao.find(id));
        }
    }

    @Dann("werden alle ([^\"]+) gelöscht")
    public void thenAllEntitiesAreDeleted(String elementName) throws Exception {
        GenericDao<?, ?> dao = null;

        if ("Aufgaben".equals(elementName)) {
            dao = taskDao;
        } else {
            fail("Element name '" + elementName + "' is undefined!");
        }

        assertEquals("All " + elementName + " should be deleted!", 0, dao.count());
    }

    @Dann("^sind die Checkboxen zu allen (?:[^\" ]+) (ausgewählt|abgewählt)$")
    public void thenAllCheckboxesAreSelectedOrNotSelected(String selection) throws Exception {
        boolean isSelected = "ausgewählt".equals(selection);

        WebElement table = driver.findElement(By.xpath("//table[@class='list']"));
        List<WebElement> checkboxes = table.findElements(By.id("add-select"));

        for (WebElement element : checkboxes) {
            assertTrue("Nicht alle Checkboxen sind " + selection, element.isSelected() == isSelected);
        }
    }
}
