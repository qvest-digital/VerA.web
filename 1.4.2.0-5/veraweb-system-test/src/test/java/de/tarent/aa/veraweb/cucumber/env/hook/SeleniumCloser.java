package de.tarent.aa.veraweb.cucumber.env.hook;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.runtime.ScenarioResult;
import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.env.SeleniumFactory;
import de.tarent.aa.veraweb.cucumber.env.StartAndShutdownHook;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerAfter;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStop;

/**
 * This class represents a hook. They destroy all selenium-instances at the shutdown.
 */
public class SeleniumCloser implements HandlerStop, HandlerAfter {

    @Autowired
    private WebDriver driver;

    @Autowired
    private GlobalConfig config;

    public SeleniumCloser() {
        // register me
        StartAndShutdownHook.addOnStopHandler(this);
        StartAndShutdownHook.addOnAfterHandler(this);
    }

    public void handleStop() {
        SeleniumFactory.closeAll();
    }

    @Override
    public void handleAfterScenario(ScenarioResult result) {
        driver.get(config.getVerawebBaseUrl() + "logout");
    }
}
