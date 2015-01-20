/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
