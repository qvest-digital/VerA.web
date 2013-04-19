package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import cucumber.annotation.de.Angenommen;
import cucumber.annotation.de.Wenn;
import de.tarent.aa.veraweb.cucumber.data.AufgabeData;
import de.tarent.aa.veraweb.cucumber.data.LoginData;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public class FormStepDefinitions extends AbstractStepDefinitions {

    @Wenn("^ich \"([^\"]*Anmeldedaten[^\"]*)\" eingebe$")
    public void whenClickElement(String name) {
        LoginData loginData = LoginData.forName(NameUtil.nameToEnumName(name));
        whenFillFields(loginData.valuesForPageFields);
    }
    
    @Angenommen("^ich fülle die Maske mit \"([^\"]*)\" aus$")
    public void whenFillMask(String name) throws Throwable {
        AufgabeData data = AufgabeData.forName(NameUtil.nameToEnumName(name));
        whenFillFields(data.valuesForPageFields);
    }

    @Wenn("^ich die Checkbox zur ([^\"]+) \"([^\"]+)\" auswähle$")
    @Angenommen("^ich wähle die Checkbox zur ([^\"]+) \"([^\"]+)\" aus$")
    public void whenClickCheckboxInTable(String element, String taskName) {
        List<WebElement> resultingObjectDataRows = driver.findElements(By.xpath("//table[@class='list']/tbody/tr"));

        int countColumns = 0;
        if ("Aufgabe".equals(element)) {
            countColumns = 8;
        }

        Integer rowIndex = null;
        for (int i = 1; i < resultingObjectDataRows.size(); i++) {
            List<WebElement> tempObjectRow = new ArrayList<WebElement>();
            tempObjectRow.addAll(resultingObjectDataRows.get(i).findElements(By.xpath(".//td[@class='list']")));

            for (int j = 0; j < countColumns; j++) {
                if (tempObjectRow.get(j).getText().equals(taskName)) {
                    rowIndex = i;
                    break;
                }
            }
        }

        if (rowIndex == null) {
            fail("Task '" + taskName + "' not found in table");
        } else {
            WebElement chkBox = resultingObjectDataRows.get(rowIndex.intValue()).findElement(By.id("add-select"));
            if (!chkBox.isSelected()) {
                chkBox.click();
            }
        }
    }
    
}
