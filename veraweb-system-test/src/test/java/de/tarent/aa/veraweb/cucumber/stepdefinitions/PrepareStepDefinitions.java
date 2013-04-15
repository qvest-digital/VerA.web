package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Angenommen;
import de.tarent.aa.veraweb.cucumber.data.LoginData;
import de.tarent.aa.veraweb.cucumber.pagedefinitions.PageDefinition;

public class PrepareStepDefinitions extends AbstractStepDefinitions {

	@Angenommen("^ich bin als User angemeldet")
	public void loggedInAsAdmin() {
	    whenNavigateToPage(PageDefinition.ANMELDUNGSSEITE);
	    whenFillField(LoginData.GUELTIGE_ANMELDEDATEN.user, page.elementForName("Benutzername-Feld"));
	    whenFillField(LoginData.GUELTIGE_ANMELDEDATEN.password, page.elementForName("Passwort-Feld"));
	}

}
