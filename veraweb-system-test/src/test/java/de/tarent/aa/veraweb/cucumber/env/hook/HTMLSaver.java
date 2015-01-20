/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.runtime.ScenarioResult;
import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.env.StartAndShutdownHook;
import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerAfter;
import de.tarent.aa.veraweb.cucumber.formatter.RuntimeInfoCatcher;

/**
 * This class represents a hook. They take the html-source after a step failed.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class HTMLSaver implements HandlerAfter {

    /**
     * Log.
     */
    private static final Log LOG = LogFactory.getLog(HTMLSaver.class);

    /**
     * Injected {@link WebDriver}.
     */
    @Autowired
    private WebDriver browser;

    /**
     * Injected {@link GlobalConfig}.
     */
    @Autowired
    private GlobalConfig config;

    /**
     * Constructor.
     */
    public HTMLSaver() {
        // register me
        StartAndShutdownHook.addOnAfterHandler(this);
    }

    /**
     * {@inheritDoc}<br/>
     * Save test result in HTML report.
     */
    public void handleAfterScenario(ScenarioResult result) {
        RuntimeInfoCatcher catcher = RuntimeInfoCatcher.getInstance();
        if (catcher == null) {
            // this can be happen if the formatter is not configured
            // or the test is running in a IDE-JUnit-Test.
            return;
        }

        if (result.isFailed()) {
            // //
            // save html-result
            // //
            if (config.getHtmlPath() != null && !"".equals(config.getHtmlPath())) {

                String htmlPath = Utils.generateOutputPath(config.getHtmlPath(), catcher.getCurrentFeature(),
                        catcher.getCurrentScenarioCount(), catcher.getCurrentStepCount());
                htmlPath += ".html";

                Utils.createDirectoryIfDoesntExist(htmlPath);

                String htmlSource = browser.getPageSource();
                try {
                    Utils.writeHtmlFile(htmlSource, htmlPath);
                } catch (IOException e) {
                    LOG.error("Error on catching html-source.", e);
                }
            }
        }
    }
}
