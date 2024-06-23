package org.dorum.automation.common.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.perfecto.CapabilityName;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.net.URL;

@Log4j2
public abstract class AbstractDriverManager {

    protected abstract WebDriver createDriver();

    protected abstract MutableCapabilities capabilities();

    public abstract DriverService startService();

    public abstract void stopService();

    protected static WebDriverManager webDriverManager;

    protected WebDriver createRemoteDriver(String host, MutableCapabilities capabilities) {
        log.info("Initializing Remote driver");
        WebDriver driver = null;
        try {
            capabilities.setCapability(CapabilityName.BROWSER_NAME, ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME));
            driver = new RemoteWebDriver(new URL(host), capabilities);
        } catch (Exception e) {
            log.warn("Invalid 'remote' parameter: {}\n{}", host, e);
        }
        return driver;
    }

    @SneakyThrows
    public URL getServiceUrl() {
        String url = ConfigProperties.getProperty(ProjectConfig.PERFECTO_URL);
        log.info("Service URL: {}", url);
        return new URL(url);
    }

    protected void getDefaultCapabilities(ChromeOptions capabilities) {
        capabilities.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        capabilities.setCapability(CapabilityName.ACCEPT_SSL_CERTS, true);
        capabilities.setAcceptInsecureCerts(true);
        capabilities.setCapability(CapabilityName.TAKES_SCREENSHOT, true);
        capabilities.setCapability(CapabilityName.HANDLES_ALERTS, true);
    }

    public void getLatestDriver(boolean download) {
        if (download) {
            webDriverManager = WebDriverManager.getInstance(DriverManagerType.CHROME)
                    .proxyUser(ConfigProperties.getProperty(ProjectConfig.PROXY_USER))
                    .proxyPass(ConfigProperties.getProperty(ProjectConfig.PROXY_PASS))
                    .proxy(ConfigProperties.getProperty(ProjectConfig.PROXY_HOST))
                    .cachePath("chromedriverWin32").forceDownload().arch32().win();
//                    .driverVersion("114");
            webDriverManager.setup();
        }
    }

    public static boolean isAndroid() {
        return ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME).equals(DriverType.ANDROID.getName());
    }

    public static boolean isIos() {
        return ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME).equals(DriverType.IOS.getName());
    }

    @SneakyThrows
    public File getLogFile(String logFileName) {
        String logFilePath = String.format("%s%starget%slogs%s%s", getCanonicalPath(),
                                           File.separator, File.separator, File.separator, logFileName);
        log.info("Log File: {}", logFilePath);
        return new File(logFilePath);
    }

  public static String getCanonicalPath() {
    try {
      return new File(".").getCanonicalPath();
    } catch (Exception e) {
      log.warn("FAILED - get canonical path\n%s", e);
    }
    return null;
  }
}
