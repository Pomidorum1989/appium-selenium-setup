package org.dorum.automation.common.driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dorum.automation.common.utils.Log;
import org.openqa.selenium.WebDriver;
import org.testng.collections.Maps;

import java.util.Map;

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
        Log.info("WebDriver with thread ID: %s - is removed", threadId);
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
