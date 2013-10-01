package de.tarent.aa.veraweb.cucumber.formatter;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cucumber.runtime.CucumberException;

/**
 * This formatter extends the cucumber HTMFormatter (Version 1.0.2). The cucumber-formatter doesn't support the nice
 * features such like &gt;summary of the results&lt; or &gt;collapse/expand one or all scenarios&lt; but this features
 * be sorely missed!
 * 
 * This formatter delegates all to the cucumber-formatter. At the least the output HTML-File is edited.
 * 
 * @author Sven Schumann <s.schumann@tarent.de>
 * @version 1.0
 * 
 */
public class HTMLFormatter implements Reporter, Formatter {

    /**
     * Path of needed JavaScript file.
     */
    private static final String EXTENDED_JS_FILE = "/extended.js";

    /**
     * Directory of html report.
     */
    private File htmlReportDir;

    /**
     * Base HTMl formatter.
     */
    private cucumber.formatter.HTMLFormatter baseFormatter;

    /**
     * Constructor.
     * 
     * @param htmlReportDir
     *            directory of html report
     */
    public HTMLFormatter(File htmlReportDir) {
        baseFormatter = new cucumber.formatter.HTMLFormatter(htmlReportDir);
        this.htmlReportDir = htmlReportDir;
    }

    /**
     * {@inheritDoc}
     */
    public void background(Background background) {
        baseFormatter.background(background);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        baseFormatter.close();
    }

    /**
     * {@inheritDoc}
     */
    public void eof() {
        baseFormatter.eof();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object arg0) {
        return baseFormatter.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public void examples(Examples examples) {
        baseFormatter.examples(examples);
    }

    /**
     * {@inheritDoc}
     */
    public void feature(Feature feature) {
        baseFormatter.feature(feature);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return baseFormatter.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public void match(Match match) {
        baseFormatter.match(match);
    }

    /**
     * {@inheritDoc}
     */
    public void result(Result result) {
        baseFormatter.result(result);
    }

    /**
     * {@inheritDoc}
     */
    public void scenario(Scenario scenario) {
        baseFormatter.scenario(scenario);
    }

    /**
     * {@inheritDoc}
     */
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        baseFormatter.scenarioOutline(scenarioOutline);
    }

    /**
     * {@inheritDoc}
     */
    public void step(Step step) {
        baseFormatter.step(step);
    }

    /**
     * {@inheritDoc}
     */
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, int line) {
        baseFormatter.syntaxError(state, event, legalEvents, uri, line);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return baseFormatter.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void uri(String uri) {
        baseFormatter.uri(uri);
    }

    /**
     * {@inheritDoc}
     */
    public void write(String text) {
        baseFormatter.write(text);
    }

    /**
     * {@inheritDoc}
     */
    public void embedding(String mimeType, byte[] data) {
        baseFormatter.embedding(mimeType, data);
    }

    /**
     * {@inheritDoc}
     */
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        baseFormatter.syntaxError(state, event, legalEvents, uri, line);
    }

    /**
     * {@inheritDoc}
     */
    public void before(Match match, Result result) {
        baseFormatter.before(match, result);
    }

    /**
     * {@inheritDoc}
     */
    public void after(Match match, Result result) {
        baseFormatter.after(match, result);
    }

    /**
     * {@inheritDoc}
     */
    public void done() {
        baseFormatter.done();

        // after super-method invocation the index.html is stored
        File oldHTML = new File(htmlReportDir.getPath() + "/index.html");
        File newHTML = mergeHTML(oldHTML);

        // replace html-files
        oldHTML.delete();
        newHTML.renameTo(new File(htmlReportDir.getPath() + "/cucumber_report.html"));
    }

    /**
     * Merge HTML report.
     * 
     * @param oldHTML
     *            old HTML file
     * @return merged HTML file
     */
    private File mergeHTML(File oldHTML) {
        File newHTML = new File(htmlReportDir.getPath() + "/index.html_new");

        BufferedReader oldHTMLReader = null;
        BufferedWriter newHTMLWriter = null;
        InputStream extendedIS = null;

        try {
            oldHTMLReader = new BufferedReader(new FileReader(oldHTML));
            newHTMLWriter = new BufferedWriter(new FileWriter(newHTML));
            extendedIS = getClass().getResourceAsStream(EXTENDED_JS_FILE);

            String oldLine = oldHTMLReader.readLine();

            while (oldLine != null) {
                String newLine = oldLine;

                if (oldLine.toLowerCase().contains("</body>")) {
                    newHTMLWriter.write("\t\t\t<script lang=\"text/javascript\">\n");

                    // copy content from js-file into new html-file
                    byte[] buffer = new byte[16 * 1024];
                    int readed = 0;
                    while ((readed = extendedIS.read(buffer)) != -1) {
                        newHTMLWriter.write(new String(buffer, 0, readed));
                    }
                    newHTMLWriter.write("\t\t\t</script>\n");
                }

                newHTMLWriter.write(newLine + "\n");
                oldLine = oldHTMLReader.readLine();
            }
        } catch (IOException e) {
            throw new CucumberException("Unable to edit index.html: ", e);
        } finally {
            try {
                if (oldHTMLReader != null) {
                    oldHTMLReader.close();
                }
                if (newHTMLWriter != null) {
                    newHTMLWriter.close();
                }
                if (extendedIS != null) {
                    extendedIS.close();
                }
            } catch (IOException e) {
                throw new CucumberException("Unable to close reader/writer/stream: ", e);
            }
        }

        return newHTML;
    }
}
