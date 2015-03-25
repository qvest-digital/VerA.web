/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import static org.junit.Assert.fail;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Angenommen;
import cucumber.annotation.de.Und;
import cucumber.annotation.de.Wenn;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;
import de.tarent.aa.veraweb.selenium.AdvancedWebDriver;

public class NavigationStepDefinitions extends AbstractStepDefinitions {

    /**
     * Injected {@link AdvancedWebDriver}.
     */
    @Autowired
    protected WebDriver driver;

    @Wenn("^ich auf \"([^\"]+)\" klicke$")
    @Angenommen("^ich klicke auf \"([^\"]+)\"$")
    public void whenClickElement(String name) {
        whenClickElement(page.elementForName(name));
    }

    @Angenommen("^ich bin (?:nicht angemeldet|abgemeldet)$")
    public void whenUserLoggedOut() {
        whenNavigateToPage(PageDefinition.ABMELDUNGSSEITE);
    }

    @Angenommen("^ich befinde mich auf der Detailansicht der Veranstaltung \"([^\"]+)\"$")
    public void whenNavigateToDetailViewOfEvent(String eventName) {
        whenClickElement(page.elementForName("Veranstaltung bearbeiten"));

        page = PageDefinition.VERANSTALTUNG_SUCHEN;
        whenFillField(eventName, page.elementForName("Kurzbeschreibung-Feld"));
        whenClickElement(page.elementForName("Suche starten"));

        WebElement webElem = driver.findElement(By.xpath("//td[text()='" + eventName + "']"));
        webElem.click();
        page = PageDefinition.VERANSTALTUNG_DETAILANSICHT;
    }

    @Wenn("^ich den Reiter \"([^\"]+)\" aufrufe$")
    @Angenommen("^ich rufe den Reiter \"([^\"]+)\" auf$")
    public void whenNavigateToTab(String tabName) {
        whenClickElement(page.elementForName(tabName));
    }

    @Wenn("^ich die Rückfrage mit \"([^\"]+)\" beantworte$")
    public void whenConfirmationDialog(String confirmation) throws Exception {
        Alert alert = driver.switchTo().alert();
        if ("JA".equals(confirmation.trim().toUpperCase())) {
            alert.accept();
        } else if ("NEIN".equals(confirmation.trim().toUpperCase())) {
            alert.dismiss();
        } else {
            fail("Confirmation response '" + confirmation + "' is undefined!");
        }
        Thread.sleep(1000L);
    }

    @Und("^ich betrachte die \"([^\"]+)\" der (?:[^\"]+)liste$")
    public void whenNavigateToTablePage(String elementName) {
        whenClickElement(page.elementForName(elementName));
    }

}
