package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Dann;
import cucumber.annotation.de.Und;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;

public class CheckStepDefinitions extends AbstractStepDefinitions {

    @Dann("^bin ich erfolgreich vom System abgemeldet$")
    public void thenSuccessfullyLoggedOut() {
        thenAtPage(PageDefinition.ABMELDUNGSSEITE);
    }

    @Dann("^sehe ich als \"([^\"]+)\" die Meldung \"([^\"]+)\"$")
    @Und("^ich sehe als \"([^\"]+)\" die Meldung \"([^\"]+)\"$")
    public void thenElementTextEquals(String elementName, String expectedText) {
        thenElementTextEquals(page.elementForName(elementName), expectedText);
    }
    
}

