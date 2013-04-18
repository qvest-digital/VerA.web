package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Wenn;
import de.tarent.aa.veraweb.cucumber.data.LoginData;
import de.tarent.aa.veraweb.cucumber.utils.NameUtil;

public class FormStepDefinitions extends AbstractStepDefinitions {

    @Wenn("^ich \"([^\"]*Anmeldedaten[^\"]*)\" eingebe$")
    public void whenClickElement(String name) {
        LoginData loginData = LoginData.forName(NameUtil.nameToEnumName(name));
        whenFillFields(loginData.valuesForPageFields);
    }
    
    @Wenn("^ich die Checkbox zur Aufgabe \"([^\"]+)\" auswähle$")
    public void whenClickCheckboxInTable(String name) {
        
    }
}
