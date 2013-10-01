package de.tarent.aa.veraweb.cucumber.env.event;

import cucumber.runtime.ScenarioResult;

/**
 * Handler for processing hooks after after each scenario.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public interface HandlerAfter {

    /**
     * This will be called after each scenario.
     * 
     * @param result
     *            {@link ScenarioResult}
     */
    public void handleAfterScenario(ScenarioResult result);
}
