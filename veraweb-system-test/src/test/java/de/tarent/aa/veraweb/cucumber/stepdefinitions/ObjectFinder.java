package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.db.entity.Task;

/**
 * Helper class for finding Objects in tables in the webinterface
 * 
 * @author Dino Omanovic (d.omanovic@tarent.de), tarent solutions GmbH
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */

@Component(value = "ObjectFinder")
public class ObjectFinder {

    @Autowired
    private WebDriver driver;

    protected void checkForObjects(List<?> objectData) {
        List<WebElement> resultingObjectDataRows = driver.findElements(By.xpath("//table[@class='list']/tbody/tr"));

        int i = 1;
        for (Object object : objectData) {
            checkForSingleObject(resultingObjectDataRows, object, i);
            if (i < resultingObjectDataRows.size()) {
                i++;
            }
        }
    }

    private void checkForSingleObject(List<WebElement> resultingObjectDataRows, Object object, int i) {
        List<WebElement> tempObjectRow = new ArrayList<WebElement>();
        tempObjectRow.addAll(resultingObjectDataRows.get(i).findElements(By.xpath(".//td[@class='list']")));
        if (Task.class.isInstance(object)) {
            checkIfTaskFound((Task) object, tempObjectRow);
        }
    }

    private void checkIfTaskFound(Task task, List<WebElement> tempTaskRow) {
        if (tempTaskRow == null || tempTaskRow.isEmpty()) {
            fail("No table rows found");
        }

        boolean chkBoxSelected = tempTaskRow.get(0).findElement(By.id("add-select")).isSelected();
        if (chkBoxSelected != task.getToDeleteSelected().booleanValue()) {
            fail("Zelleneintag stimmt nicht überein: " + chkBoxSelected + " != " + task.getToDeleteSelected());
        }
        if (!tempTaskRow.get(1).getText().equals(String.valueOf(task.getId()))) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(1).getText() + " != " + task.getId());
        }
        if (!tempTaskRow.get(2).getText().equals(task.getTitle())) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(2).getText() + " != " + task.getTitle());
        }
        String startDate = Utils.formatVerawebDate(task.getStartDate());
        if (!tempTaskRow.get(3).getText().equals(startDate)) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(3).getText() + " != " + startDate);
        }
        String endDate = Utils.formatVerawebDate(task.getEndDate());
        if (!tempTaskRow.get(4).getText().equals(endDate)) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(4).getText() + " != " + endDate);
        }
        if (!tempTaskRow.get(5).getText().equals(String.valueOf(task.getDegreeOfCompletion()) + "%")) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(5).getText() + " != "
                    + task.getDegreeOfCompletion());
        }
        if (task.getResponsiblePerson() != null) {
            String responsiblePersonName = task.getResponsiblePerson().getLastName() + ", "
                    + task.getResponsiblePerson().getFirstName();
            if (!tempTaskRow.get(6).getText().equals(responsiblePersonName)) {
                fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(6).getText() + " != "
                        + responsiblePersonName);
            }
        }
        if (!tempTaskRow.get(7).getText().equals(String.valueOf(task.getPriority()))) {
            fail("Zelleneintag stimmt nicht überein: " + tempTaskRow.get(7).getText() + " != " + task.getPriority());
        }
    }

}
