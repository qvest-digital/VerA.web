package de.tarent.aa.veraweb.cucumber.env.event;

/**
 * Handler for processing hooks before each scenario.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public interface HandlerBefore {

    /**
     * This will be called before each scenario.
     */
    public void handleBeforeScenario();
}
