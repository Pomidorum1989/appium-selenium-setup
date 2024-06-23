package org.dorum.automation.common.driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.collections.Maps;

import java.util.Map;
@Log4j2

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebDriverContainer {

    private static final Map<Long, WebDriver> threadWebDriver = Maps.newConcurrentMap();

    public static WebDriver getDriver() {
        long threadId = Thread.currentThread().getId();
        if (!threadWebDriver.containsKey(threadId)) {
            throw new IllegalStateException(String.format("No WebDriver is bound to current thread: %s", threadId));
        } else {
            return threadWebDriver.get(threadId);
        }
    }

    public static void setDriver(WebDriver driver) {
        resetWebDriver();
        threadWebDriver.put(Thread.currentThread().getId(), driver);
    }

    public static void removeDriver() {
        long threadId = Thread.currentThread().getId();
        threadWebDriver.remove(threadId);
        log.info("WebDriver with thread ID: {} - is removed", threadId);
    }

    public static boolean hasWebDriverStarted() {
        return threadWebDriver.get(Thread.currentThread().getId()) != null;
    }

    private static void resetWebDriver() {
        if (hasWebDriverStarted()) {
            threadWebDriver.remove(Thread.currentThread().getId());
        }
    }
}
