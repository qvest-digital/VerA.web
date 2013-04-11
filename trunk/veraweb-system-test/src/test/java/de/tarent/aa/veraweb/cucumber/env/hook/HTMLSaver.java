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
