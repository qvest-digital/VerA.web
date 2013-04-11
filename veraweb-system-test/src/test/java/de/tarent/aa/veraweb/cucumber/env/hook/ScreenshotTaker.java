package de.tarent.aa.veraweb.cucumber.env.hook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.runtime.ScenarioResult;
import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.env.StartAndShutdownHook;
import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerAfter;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStop;
import de.tarent.aa.veraweb.cucumber.formatter.RuntimeInfoCatcher;

/**
 * This class represents a hook. They take a screenshot after a step failed.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class ScreenshotTaker implements HandlerAfter, HandlerStop {

    /**
     * Name of JavaScript file needed for reporting.
     */
    private static final String JS_REPORT_NAME = "report.js";

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
     * List of embedded paths.
     */
    private List< String > embeddedPath;

    /**
     * Constructor.
     */
    public ScreenshotTaker() {
        // register me
        StartAndShutdownHook.addOnAfterHandler(this);
        StartAndShutdownHook.addOnStopHandler(this);

        embeddedPath = new ArrayList< String >();
    }

    /**
     * {@inheritDoc}.<br/>
     * Take a screenshot after a step failed.
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
            // save a screenshot
            // //
            byte[] screenshot = null;

            boolean toSeperateDirectory = config.getScreenshotPath() != null && !config.getScreenshotPath().equals("");
            boolean embedding = config.isScreenshotEmbedded();
            boolean successfullyTaken = true;

            if (toSeperateDirectory || embedding) {

                // check webdriver for screenshot-functionality
                if (browser instanceof TakesScreenshot) {
                    screenshot = ((TakesScreenshot) browser).getScreenshotAs(OutputType.BYTES);
                }

                if (screenshot == null || screenshot.length == 0) {
                    System.err.println("Error on taking screenshot. Apparently this functionality is not available.");
                    successfullyTaken = false;
                }
            }

            if (toSeperateDirectory && successfullyTaken) {
                String screenshotPath =
                    Utils.generateOutputPath(config.getScreenshotPath(), catcher.getCurrentFeature(),
                        catcher.getCurrentScenarioCount(), catcher.getCurrentStepCount());
                screenshotPath += ".png";

                // we need a mapping between embedded- and separate path
                embeddedPath.add(screenshotPath);
                Utils.createDirectoryIfDoesntExist(screenshotPath);

                try {
                    Utils.writeByteArrayToFile(screenshot, screenshotPath);
                } catch (IOException e) {
                    System.err.println("Error on taking screenshot.");
                    e.printStackTrace();
                }
            }

            if (embedding && successfullyTaken) {
                byte[] data = null;
                if (toSeperateDirectory) {
                    // to minimize the requirements we embed a "fake"-file
                    // because we delete it anyway
                    data = new byte[] {};
                } else {
                    data = screenshot;
                }

                result.embed(data, "image/png");
            }
        }
    }

    /**
     * If we have declared a separate directory for screenshots we don't need to store embedded-files too. The problem
     * is, that we can't hook at the mechanism which declare where the screenshots are stored. A solution is, that we
     * edit the output report.js file. We search for 'embedded.png' pattern and replace it with the path of the separate
     * directory. At the least we remove all embedded-files and replace the edited report.js with the original.
     * 
     * To realize this feature we need a mapping between embedded- and separate path. The HTML-Formatter store the
     * embedded files simple: start with "embedded" followed by a number (which starts at 0 and increased by one for
     * each embedded file) followed by the file prefix such like ".png".
     */
    public void handleStop() {
        boolean toSeperateDirectory = config.getScreenshotPath() != null && !config.getScreenshotPath().equals("");
        boolean embedding = config.isScreenshotEmbedded();
        if (!(toSeperateDirectory && embedding)) {
            // only at a seperate directory we need to edit report.js
            return;
        }

        File jsReport = new File(config.getResultContentPath() + "/" + JS_REPORT_NAME);
        File newJsReport = new File(config.getResultContentPath() + "/new_" + JS_REPORT_NAME);
        String relativeScreenshotDir = config.getScreenshotPath().replace(config.getResultContentPath(), "");
        if (relativeScreenshotDir.startsWith("/")) {
            // no "/" at the begin
            relativeScreenshotDir = relativeScreenshotDir.substring(1);
        }

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            // ensure that we have always a fresh jsReport
            if (newJsReport.exists())
                newJsReport.delete();
            newJsReport.createNewFile();

            br = new BufferedReader(new FileReader(jsReport));
            bw = new BufferedWriter(new FileWriter(newJsReport));

            // we look for this pattern
            Pattern p = Pattern.compile("\\'embedded([0-9]*).png\\'");

            String line = null;
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);

                if (m.find()) {
                    // if we find a embedded-path
                    final String pre = line.substring(0, m.start() + 1);
                    final String post = line.substring(m.end() - 1);
                    final String correspondingScreenshotPath = embeddedPath.get(Integer.parseInt(m.group(1)));
                    final String screenshotPathPart =
                        correspondingScreenshotPath.replace(config.getScreenshotPath(), "");

                    // the new path must look like "screenshots/feature1/fail1.png"
                    // the important thing is that the path is a RELATIVE path
                    // (a path which is not start with a slash)
                    line = pre;
                    line += relativeScreenshotDir;
                    line += "/" + screenshotPathPart;
                    line += post;
                }

                bw.write(line + "\n");
            }

            br.close();
            bw.close();

            // replace new report with old
            newJsReport.renameTo(jsReport);

            // remove all embedded files
            File[] toRemove = new File(config.getResultContentPath()).listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("^embedded.*");
                }
            });
            if (toRemove != null)
                for (File f : toRemove) {
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
