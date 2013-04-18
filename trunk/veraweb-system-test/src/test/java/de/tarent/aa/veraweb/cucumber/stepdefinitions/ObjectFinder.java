package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.db.entity.Task;
import de.tarent.aa.veraweb.selenium.AdvancedWebDriver;

/**
 * Helper class for finding Objects in tables in the webinterface
 * 
 * @author Dino Omanovic (d.omanovic@tarent.de), tarent solutions GmbH
 */

@Component(value = "ObjectFinder")
public class ObjectFinder {

    @Autowired
    private GlobalConfig config;

    @Autowired
    private WebDriver driver;

    @Autowired
    private AdvancedWebDriver advancedBrowser;

    protected void checkForObjects(List<?> objectData) {
        List<WebElement> resultingObjectDataRows = driver.findElements(By.xpath("//table[@class='list']/tbody/tr"));

        int foundObjects = 0;

        for (Object object : objectData) {
            for (int i = 0; i < resultingObjectDataRows.size(); i++) {
                foundObjects = checkForSingleObject(resultingObjectDataRows, foundObjects, object, i);
            }
        }

        // assertTrue(foundObjects == objectData.size());
    }

    private int checkForSingleObject(List<WebElement> resultingObjectDataRows, int foundObjects, Object object, int i) {
        List<WebElement> tempObjectRow = new ArrayList<WebElement>();
        tempObjectRow.addAll(resultingObjectDataRows.get(i).findElements(By.xpath("//td[@class='list']")));
        if (Task.class.isInstance(object)) {
            foundObjects = checkIfTaskFound(foundObjects, (Task) object, tempObjectRow);
        }
        return foundObjects;
    }

    private int checkIfTaskFound(int foundTasks, Task task, List<WebElement> tempTaskRow) {
        if (tempTaskRow == null || tempTaskRow.isEmpty()) {
            fail("No table rows not found");
        }
        if (tempTaskRow.get(0).findElement(By.id("add-select")).isSelected() == task.getToDeleteSelected()
                .booleanValue()
                && tempTaskRow.get(1).getText().equals(String.valueOf(task.getId()))
                && tempTaskRow.get(2).getText().equals(task.getTitle())
                && tempTaskRow.get(3).getText().equals(task.getStartDate())
                && tempTaskRow.get(4).getText().equals(task.getEndDate())
                && tempTaskRow.get(5).getText().equals(String.valueOf(task.getDegreeOfCompletion()))
                && tempTaskRow
                        .get(6)
                        .getText()
                        .equals(task.getResponsiblePerson().getLastName() + ", "
                                + task.getResponsiblePerson().getFirstName())
                && tempTaskRow.get(7).getText().equals(String.valueOf(task.getPriority()))) {
            foundTasks++;
        }
        return foundTasks;
    }

}
