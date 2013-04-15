package de.tarent.aa.veraweb.cucumber.stepdefinitions;

import cucumber.annotation.de.Wenn;

public class NavigationStepDefinitions extends AbstractStepDefinitions {
    
    @Wenn("^ich (?:den|die|das) \"([^\"]+)\" klicke")
    public void whenClickElement(String elementName) {
        whenClickElement(page.elementForName(elementName));
    }

}
