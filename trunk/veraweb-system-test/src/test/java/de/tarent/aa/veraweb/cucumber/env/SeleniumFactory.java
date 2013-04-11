package de.tarent.aa.veraweb.cucumber.env;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import de.tarent.aa.veraweb.cucumber.env.hook.SeleniumCloser;
import de.tarent.aa.veraweb.selenium.RemoteWebDriverWithScreenshotFunctionality;

/**
 * Factory for Selenium's {@link WebDriver} instances.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 */
public class SeleniumFactory {

    /**
     * Default implicitly wait time for web driver.
     */
    static final int DEFAULT_SLEEP_TIME = 10;

    /**
     * Browser name of Firefox.
     */
    static final String BROWSER_NAME_FIREFOX = "firefox";

    /**
     * Browser name of Internet Explorer.
     */
    static final String BROWSER_NAME_INTERNET_EXPLORER = "internet explorer";

    /**
     * Browser name of Safari.
     */
    static final String BROWSER_NAME_SAFARI = "safari";

    /**
     * Browser name of Chrome.
     */
    static final String BROWSER_NAME_CHROME = "chrome";

    /**
     * Browser name of Opera.
     */
    static final String BROWSER_NAME_OPERA = "opera";

    /**
     * Mode for remote Firefox web driver.
     */
    static final String BROWSER_MODE_REMOTE_FIREFOX = "ff";

    /**
     * Mode for remote Internet Explorer web driver.
     */
    static final String BROWSER_MODE_REMOTE_INTERNET_EXPLORER = "ie";

    /**
     * Mode for remote Chrome web driver.
     */
    static final String BROWSER_MODE_REMOTE_CHROME = "chrome";

    /**
     * Mode for remote Opera web driver.
     */
    static final String BROWSER_MODE_REMOTE_OPERA = "opera";

    /**
     * Mode for remote HTML Unit web driver.
     */
    static final String BROWSER_MODE_REMOTE_HTML_UNIT = "htmlunit";

    /**
     * Mode for local Firefox web driver.
     */
    static final String BROWSER_MODE_LOCAL_FIREFOX = "local-firefox";

    /**
     * Cach for created web driver instances.
     */
    private static Map< String , WebDriver > alwaysCreated = new HashMap< String , WebDriver >();

    /**
     * Build local Firefox web driver.
     * 
     * @param sleepTime
     *            sleep time
     * @param sleepUnit
     *            time unit
     * @return {@link FirefoxDriver}
     */
    public WebDriver buildLocalFF(long sleepTime, TimeUnit sleepUnit) {
        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(sleepTime, sleepUnit);
        alwaysCreated.put(BROWSER_MODE_LOCAL_FIREFOX, driver);
        return driver;
    }

    /**
     * Build remote Firefox web driver.
     * 
     * @param host
     *            host of selenium server
     * @param port
     *            port of selenium server
     * @param browserMode
     *            browser mode
     * @return remote
     */
    public WebDriver buildRemote(String host, int port, String browserMode) {
        return buildRemote(host, port, browserMode, DEFAULT_SLEEP_TIME, TimeUnit.SECONDS);
    }

    /**
     * Build remote web driver based on the given parameters.
     * 
     * @param host
     *            host of selenium server
     * @param port
     *            port of selenium server
     * @param browserMode
     *            browser mode
     * @param sleepTime
     *            sleep time
     * @param sleepUnit
     *            sleep unit
     * @return {@link WebDriver}
     */
    public WebDriver buildRemote(String host, int port, String browserMode, long sleepTime, TimeUnit sleepUnit) {
        return buildRemote(host, port, browserMode, sleepTime, sleepUnit, true);
    }

    /**
     * Build remote web driver based on the given parameters.
     * 
     * @param host
     *            host of selenium server
     * @param port
     *            port of selenium server
     * @param browserMode
     *            browser mode
     * @param sleepTime
     *            sleep time
     * @param sleepUnit
     *            sleep unit
     * @param loadFromCache
     *            load from cache or not
     * @return {@link WebDriver}
     */
    public WebDriver buildRemote(String host, int port, String browserMode, long sleepTime, TimeUnit sleepUnit,
        boolean loadFromCache) {
        DesiredCapabilities capabilities = null;

        String uniqueId = generateUniqueString(host, port, browserMode);
        if (loadFromCache) {
            for (String curKey : alwaysCreated.keySet()) {
                if (curKey.startsWith(uniqueId)) {
                    return alwaysCreated.get(curKey);
                }
            }
        }

        // Firefox
        if (browserMode.toLowerCase().startsWith(BROWSER_MODE_REMOTE_FIREFOX)) {
            capabilities = DesiredCapabilities.firefox();
            capabilities.setBrowserName(BROWSER_NAME_FIREFOX);
        }
        // InternetExplorer
        else if (browserMode.toLowerCase().startsWith(BROWSER_MODE_REMOTE_INTERNET_EXPLORER)) {
            capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            capabilities.setBrowserName(BROWSER_NAME_INTERNET_EXPLORER);
        }
        // Chrome
        else if (browserMode.toLowerCase().startsWith(BROWSER_MODE_REMOTE_CHROME)) {
            capabilities = DesiredCapabilities.chrome();
            capabilities.setBrowserName(BROWSER_NAME_CHROME);
        }
        // Opera
        else if (browserMode.toLowerCase().startsWith(BROWSER_MODE_REMOTE_OPERA)) {
            capabilities = DesiredCapabilities.opera();
            capabilities.setBrowserName(BROWSER_NAME_OPERA);
        }
        // HTML-Unit (with javascript)
        else if (browserMode.toLowerCase().startsWith(BROWSER_MODE_REMOTE_HTML_UNIT)) {
            capabilities = DesiredCapabilities.htmlUnit();
        } else {
            return null;
        }

        capabilities.setJavascriptEnabled(true);

        WebDriver driver = null;
        try {
            driver =
                new RemoteWebDriverWithScreenshotFunctionality(new URL("http://" + host + ":" + String.valueOf(port)
                    + "/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        driver.manage().timeouts().implicitlyWait(sleepTime, sleepUnit);

        // put into cache-map
        String key = uniqueId;
        for (int i = 0; alwaysCreated.containsKey(key); i++) {
            key = uniqueId + "_" + i;
        }
        alwaysCreated.put(key, driver);

        return driver;
    }

    /**
     * Generates unique string based on given parameters.
     * 
     * @param host
     *            host of selenium server
     * @param port
     *            port of selenium server
     * @param browserMode
     *            browser mode
     * @return unique string
     */
    private static String generateUniqueString(String host, int port, String browserMode) {
        return host + "_" + port + "_" + browserMode;
    }

    /**
     * Closing all created Selenium web driver. This method will be called when are integration tests finished.
     * 
     * @see SeleniumCloser
     */
    public static void closeAll() {
        for (WebDriver driver : alwaysCreated.values()) {
            driver.quit();
        }
    }
}
