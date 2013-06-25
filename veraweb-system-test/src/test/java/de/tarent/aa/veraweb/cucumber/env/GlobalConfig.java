package de.tarent.aa.veraweb.cucumber.env;

import java.util.concurrent.TimeUnit;

/**
 * Global configuration parameters needed for cucumber integration tests.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class GlobalConfig {

	/**
	 * Singleton instance of {@link GlobalConfig}.
	 */
	private static GlobalConfig instance;

	/**
	 * Host name of selenium server.
	 */
	private String serverHost;

	/**
	 * Port of selenium server.
	 */
	private int port;

	/**
	 * Browser used in selenium. Take a look at the {@link SeleniumFactory} (at the moment supported: 'FF', 'IE',
	 * 'HTMLUNIT').
	 */
	private String browserMode;

	/**
	 * Path where the HTML-source should be stored.
	 */
	private String htmlPath;

	/**
	 * Flag for embed screenshots into the HTML-report.
	 */
	private boolean screenshotEmbedded = true;

	/**
	 * Path where the screenshots should be stored.
	 */
	private String screenshotPath;

	/**
	 * Path where the test reports should be stored.
	 */
	private String resultContentPath;

	/**
	 * Path where the packaged (ZIP) cucumber test results should be stored.
	 */
	private String resultContentZipPath;

	/**
	 * Specifies the amount of time the WebDriver should wait when searching for an element if it is not immediately
	 * present.
	 */
	private long sleepTime;

	/**
	 * Specifies the time unit used to set WebDriver's implicitly wait time.
	 */
	private TimeUnit sleepUnit;

	/**
	 * Flag for new session for each feature.
	 */
	private boolean newSessionPerFeature = false;

	/**
	 * Base URL of the web application 'VerA.web' to test.
	 */
	private String verawebBaseUrl;

	/**
	 * Constructor.
	 */
	public GlobalConfig() {
		instance = this;
	}

	/**
	 * Get singleton instance.
	 * 
	 * @return singleton instance of {@link GlobalConfig}
	 */
	public static GlobalConfig getInstance() {
		return instance;
	}

	/**
	 * Checks whether if Internet Explorer is used as browser mode.
	 * 
	 * @return <code>true</code> if Internet Explorer is used as browser mode, otherwise <code>false</code>
	 */
	public boolean isInternetExplorerInUse() {
		return browserMode.toLowerCase().contains(SeleniumFactory.BROWSER_MODE_REMOTE_INTERNET_EXPLORER);
	}

	/**
	 * Set the {@link #serverHost}.
	 * 
	 * @return server host
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * Set the {@link #serverHost}.
	 * 
	 * @param serverHost
	 *            server host
	 */
	public void setServerHost(final String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * Get the {@link #port}.
	 * 
	 * @return port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the {@link #port}.
	 * 
	 * @param port
	 *            port
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Get the {@link #browserMode}.
	 * 
	 * @return browser mode
	 */
	public String getBrowserMode() {
		return browserMode;
	}

	/**
	 * Set the {@link #browserMode}.
	 * 
	 * @param browserMode
	 *            browser mode
	 */
	public void setBrowserMode(final String browserMode) {
		this.browserMode = browserMode;
	}

	/**
	 * Get the {@link #resultContentPath}.
	 * 
	 * @return path to contents of test results
	 */
	public String getResultContentPath() {
		return resultContentPath;
	}

	/**
	 * Set the {@link #resultContentPath}.
	 * 
	 * @param resultContentPath
	 *            path to contents of test results
	 */
	public void setResultContentPath(final String resultContentPath) {
		this.resultContentPath = resultContentPath;
	}

	/**
	 * Get the {@link #resultContentZipPath}.
	 * 
	 * @return path to packaged (ZIP) test results.
	 */
	public String getResultContentZipPath() {
		return resultContentZipPath;
	}

	/**
	 * Set the {@link #resultContentZipPath}.
	 * 
	 * @param resultContentZipPath
	 *            path to packaged (ZIP) test results
	 */
	public void setResultContentZipPath(final String resultContentZipPath) {
		this.resultContentZipPath = resultContentZipPath;
	}

	/**
	 * Get the {@link #htmlPath}.
	 * 
	 * @return path to HTML sources
	 */
	public String getHtmlPath() {
		return htmlPath;
	}

	/**
	 * Set the {@link #htmlPath}.
	 * 
	 * @param htmlPath
	 *            path to HTML sources
	 */
	public void setHtmlPath(final String htmlPath) {
		this.htmlPath = htmlPath;
	}

	/**
	 * Get the {@link #screenshotPath}.
	 * 
	 * @return path to screenshots
	 */
	public String getScreenshotPath() {
		return screenshotPath;
	}

	/**
	 * Set the {@link #screenshotPath}.
	 * 
	 * @param screenshotPath
	 *            path to screenshots
	 */
	public void setScreenshotPath(final String screenshotPath) {
		this.screenshotPath = screenshotPath;
	}

	/**
	 * Get the {@link #sleepTime}.
	 * 
	 * @return sleep time
	 */
	public long getSleepTime() {
		return sleepTime;
	}

	/**
	 * Set the {@link #sleepTime}.
	 * 
	 * @param sleepTime
	 *            sleep time
	 */
	public void setSleepTime(final long sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * Get the {@link #sleepUnit}.
	 * 
	 * @return sleep unit
	 */
	public TimeUnit getSleepUnit() {
		return sleepUnit;
	}

	/**
	 * Set the {@link #sleepUnit}.
	 * 
	 * @param sleepUnit
	 *            sleep unit
	 */
	public void setSleepUnit(final TimeUnit sleepUnit) {
		this.sleepUnit = sleepUnit;
	}

	/**
	 * Checks whether screenshot should be embed.
	 * 
	 * @return <code>true</code> if screenshot should be embed, otherwise <code>false</code>
	 */
	public boolean isScreenshotEmbedded() {
		return screenshotEmbedded;
	}

	/**
	 * Set the {@link #screenshotEmbedded}.
	 * 
	 * @param screenshotEmbedded
	 *            flag whether screenshot should be embed
	 */
	public void setScreenshotEmbedded(final boolean screenshotEmbedded) {
		this.screenshotEmbedded = screenshotEmbedded;
	}

	/**
	 * Checks whether new session should be used for each feature.
	 * 
	 * @return <code>true</code> if new session should be used for each feature, otherwise <code>false</code>
	 */
	public boolean isNewSessionPerFeature() {
		return newSessionPerFeature;
	}

	/**
	 * Set the {@link #newSessionPerFeature}.
	 * 
	 * @param newSessionPerFeature
	 *            flag whether new session should be used for each feature
	 */
	public void setNewSessionPerFeature(final boolean newSessionPerFeature) {
		this.newSessionPerFeature = newSessionPerFeature;
	}

	/**
	 * Get the {@link #verawebBaseUrl}.
	 * 
	 * @return base url of web application 'VerA.web'
	 */
	public String getVerawebBaseUrl() {
		return verawebBaseUrl;
	}

	/**
	 * Set the {@link #verawebBaseUrl}.
	 * 
	 * @param verawebBaseUrl
	 *            base url of web application 'VerA.web'
	 */
	public void setVerawebBaseUrl(final String verawebBaseUrl) {
		this.verawebBaseUrl = verawebBaseUrl;
	}

}
