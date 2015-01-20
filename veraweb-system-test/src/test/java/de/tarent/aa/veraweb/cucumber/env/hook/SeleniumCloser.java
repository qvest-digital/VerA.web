/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
