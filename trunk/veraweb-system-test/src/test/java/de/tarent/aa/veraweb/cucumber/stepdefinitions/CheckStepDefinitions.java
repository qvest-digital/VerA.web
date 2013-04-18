package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Dann;
import cucumber.annotation.de.Und;
import cucumber.table.DataTable;
import de.tarent.aa.veraweb.cucumber.env.EntityMapping;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.db.entity.Task;

public class CheckStepDefinitions extends AbstractStepDefinitions {

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
        objectFinder.checkForObjects(tasks);
    }

}
