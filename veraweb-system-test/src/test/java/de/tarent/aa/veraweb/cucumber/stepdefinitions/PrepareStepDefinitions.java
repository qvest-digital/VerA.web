package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Angenommen;
import de.tarent.aa.veraweb.cucumber.data.LoginData;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;

public class PrepareStepDefinitions extends AbstractStepDefinitions {
    
	@Angenommen("^ich bin (?:als User |als Administrator )angemeldet")
	public void loggedInAsAdmin() {
	    whenNavigateToPage(PageDefinition.ANMELDUNGSSEITE);
	    whenFillFields(LoginData.forName("GUELTIGE_ANMELDEDATEN").valuesForPageFields);
	    whenClickElement(PageDefinition.ANMELDUNGSSEITE.elementForName("Anmelden"));
	    
	    thenAtPage(PageDefinition.STARTSEITE_ANGEMELDET);
	    page = PageDefinition.STARTSEITE_ANGEMELDET;
	}

}
