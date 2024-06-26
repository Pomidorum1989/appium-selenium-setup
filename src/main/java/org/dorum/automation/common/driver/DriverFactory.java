package org.dorum.automation.common.driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.common.utils.listeners.PerformanceEventListener;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.concurrent.TimeUnit;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final PerformanceEventListener LISTENER = new PerformanceEventListener();

    public static synchronized WebDriver getDriver() {
        if (driver.get() == null) {
            log.info("DriverFactory: creating WebDriver");
            try {
                driver.set(createWebDriver());
                driver.set(getWebDriverEventListener());
                addWebDriverConfiguration(false);
                WebDriverContainer.setDriver(driver.get());
            } catch (Exception e) {
                log.warn("FAILED - create Web Driver\n%s", e);
            }
        }
        log.info("DriverFactory: getting WebDriver");
        return driver.get();
    }

    private static EventFiringWebDriver getWebDriverEventListener() {
        log.info("Web driver event listener is registered");
        return new EventFiringWebDriver(getDriver()).register(LISTENER);
    }

    public static void registerEventListener() {
        EventFiringWebDriver eventDriver = ((EventFiringWebDriver) WebDriverContainer.getDriver()).register(LISTENER);
        WebDriverContainer.setDriver(eventDriver);
        log.info("Web driver event listener is enabled");
    }

    public static void unRegisterEventListener() {
        log.info("DriverFactory: un-registering Event Listener");
        EventFiringWebDriver eventDriver = ((EventFiringWebDriver) WebDriverContainer.getDriver()).unregister(LISTENER);
        WebDriverContainer.setDriver(eventDriver);
        log.info("Web driver event listener is disabled");
    }

    public static synchronized void quitDriver() {
        log.info("DriverFactory: quit/close WebDriver");
        if (driver.get() != null) {
            try {
                driver.get().quit();
                driver.remove();
                WebDriverContainer.removeDriver();
            } catch (Exception e) {
                log.warn("FAILED - quit WebDriver\n%s", e);
            }
        } else {
            log.info("WebDriver is already closed");
        }
    }

    public static AbstractDriverManager getDriverManager() {
        return DriverType.getDriverByName(ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME)).getDriverManager();
    }

    private static WebDriver createWebDriver() {
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_REMOTE))) {
            return getDriverManager().createRemoteDriver(ConfigProperties.getProperty(ProjectConfig.HOST),
                                                         getDriverManager().capabilities());
        }
        return getDriverManager().createDriver();
    }

    private static synchronized void addWebDriverConfiguration(boolean isActivate) {
        if (isActivate) {
            driver.get().manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
            driver.get().manage().timeouts().setScriptTimeout(10000, TimeUnit.MILLISECONDS);
            driver.get().manage().timeouts().pageLoadTimeout(20000, TimeUnit.MILLISECONDS);
            log.info("Driver configurations are set");
        }
    }

    public synchronized static void setBrowserSize() {
        String browserSize = ConfigProperties.getProperty(ProjectConfig.BROWSER_SIZE);
        if (browserSize != null && !Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_MAXIMIZED))) {
            log.info("Set browser size to: {}", browserSize);
            String[] dimension = browserSize.split("x");
            int width = Integer.parseInt(dimension[0]);
            int height = Integer.parseInt(dimension[1]);
            driver.get().manage().window().setSize(new Dimension(width, height));
        } else {
            driver.get().manage().window().maximize();
        }
    }
}
