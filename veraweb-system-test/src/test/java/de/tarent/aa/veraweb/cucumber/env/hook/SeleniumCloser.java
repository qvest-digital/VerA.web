package de.tarent.aa.veraweb.cucumber.env.hook;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import de.deutschepost.e42.dnsa.cucumber.env.SeleniumFactory;
import de.deutschepost.e42.dnsa.cucumber.env.StartAndShutdownHook;
import de.deutschepost.e42.dnsa.cucumber.env.event.HandlerStop;

/**
 * This class represents a hook. They destroy all selenium-instances at the shutdown.
 */
public class SeleniumCloser implements HandlerStop {

    @Autowired
    WebDriver driver;

    public SeleniumCloser() {
        // register me
        StartAndShutdownHook.addOnStopHandler(this);
    }

    public void handleStop() {
        SeleniumFactory.closeAll();
    }
}
