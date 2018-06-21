import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.os.ExecutableFinder
import org.openqa.selenium.remote.CapabilityType

import static org.apache.commons.lang3.SystemUtils.*

// configure waiting
waiting {
    timeout = 10
    retryInterval = 0.1
}

// enable waiting for page object's at-checks
atCheckWaiting = true

def width = 1440
def height = 900

File findDriverExecutable() {
    def defaultExecutable = new ExecutableFinder().find("chromedriver")
    if (defaultExecutable) {
        new File(defaultExecutable)
    } else {
        driverDir().listFiles().findAll {
            !it.name.endsWith(".version")
        }.find {
            if (IS_OS_LINUX) {
                it.name.contains("linux")
            } else if (IS_OS_MAC) {
                it.name.contains("mac")
            } else if (IS_OS_WINDOWS) {
                it.name.contains("windows")
            }
        }
    }
}

driver = {
    ChromeDriverService.Builder serviceBuilder = new ChromeDriverService.Builder()
            .usingAnyFreePort()
            .usingDriverExecutable(findDriverExecutable())

    ChromeOptions chromeOptions = new ChromeOptions()
    chromeOptions.addArguments("window-size=${width},${height}")

    chromeOptions.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS, 'true')
    chromeOptions.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, 'true')
    chromeOptions.setCapability(CapabilityType.TAKES_SCREENSHOT, 'true')

    new ChromeDriver(serviceBuilder.build(), chromeOptions)
}

File driverDir() {
    String path = '../systemtest/drivers'
    File dir = new File(path)
    int depth = 0
    while (!dir.exists() && depth < 2) {
        path = "../$path"
        dir = new File(path)
        depth++
    }
    dir.exists()? dir: new File()
}

baseUrl = "https://localhost"
reportsDir = 'target/geb-reports'
