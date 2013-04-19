package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.annotation.de.Angenommen;
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
        page = page.elementForName(tabName).nextPageDefinition;
    }

}
