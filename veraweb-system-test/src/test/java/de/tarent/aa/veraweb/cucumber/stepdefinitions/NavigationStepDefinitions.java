package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Angenommen;
import cucumber.annotation.de.Wenn;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;

public class NavigationStepDefinitions extends AbstractStepDefinitions {    
    
    @Wenn("^ich auf \"([^\"]+)\" klicke$")
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
        
        
    }

}
