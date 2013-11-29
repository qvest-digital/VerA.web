package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Angenommen;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.selenium.AdvancedWebDriver;

public class TaskDetailStepDefinitions extends AbstractStepDefinitions {

	/**
	 * Injected {@link AdvancedWebDriver}.
	 */
	@Autowired
	protected WebDriver driver;

	@Angenommen("^ich klicke auf die Aufgabe mit der ID (\\d+)$")
	public void ich_klicke_auf_die_Aufgabe_mit_der_ID(int id) throws Throwable {
		driver.findElement(By.xpath("//td[text()='" + id + "']")).click();
		page = PageDefinition.AUFGABEDETAILSEITE;
	}

}
