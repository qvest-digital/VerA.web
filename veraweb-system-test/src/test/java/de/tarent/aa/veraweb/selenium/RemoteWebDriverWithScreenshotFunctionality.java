package de.tarent.aa.veraweb.selenium;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * The standart RemoteWebDriver doesn't support taking screenshots. (because they is not an instance of
 * {@link TakesScreenshot})
 * 
 * @author Sven Schumann <s.schumann@tarent.de>
 * @version 1.0
 * 
 */
public class RemoteWebDriverWithScreenshotFunctionality extends RemoteWebDriver implements TakesScreenshot {

    /**
     * Constructor.
     */
    public RemoteWebDriverWithScreenshotFunctionality() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param desiredCapabilities
     *            desired capabilities
     */
    public RemoteWebDriverWithScreenshotFunctionality(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    /**
     * Constructor.
     * 
     * @param executor
     *            executor
     * @param desiredCapabilities
     *            desired capabilities
     */
    public RemoteWebDriverWithScreenshotFunctionality(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
    }

    /**
     * Constructor.
     * 
     * @param remoteAddress
     *            remote address
     * @param desiredCapabilities
     *            desired capabilities
     */
    public RemoteWebDriverWithScreenshotFunctionality(URL remoteAddress, Capabilities desiredCapabilities) {
        super(remoteAddress, desiredCapabilities);
    }

    /**
     * {@inheritDoc}.
     */
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if ((Boolean) getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
            String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();
            return target.convertFromBase64Png(base64Str);
        }
        return null;
    }

}
