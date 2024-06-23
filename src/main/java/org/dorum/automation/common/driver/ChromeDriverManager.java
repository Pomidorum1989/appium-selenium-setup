package org.dorum.automation.common.driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dorum.automation.perfecto.CapabilityName;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.dorum.automation.perfecto.CapabilityName.*;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChromeDriverManager extends AbstractDriverManager {

    private static ChromeDriverService chService;

    private static class SingletonHolder {
        public static final ChromeDriverManager INSTANCE = new ChromeDriverManager();
    }

    public static ChromeDriverManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ChromeDriverService startService() {
        getLatestDriver(false);
        if (null == chService) {
            try {
                chService = ChromeDriverService.createDefaultService();
            } catch (Exception e) {
                log.warn("FAILED - start Service\n%s", e);
            }
        }
        return chService;
    }

    public void stopService() {
        if ((null != chService) && chService.isRunning()) {
            chService.stop();
        }
    }

    @Override
    protected WebDriver createDriver() {
        log.info("Initializing Chrome Driver");
        return new ChromeDriver((ChromeOptions) capabilities());
    }

    @Override
    protected MutableCapabilities capabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        ChromeOptions options = new ChromeOptions();
        getDefaultCapabilities(options);
        options.addArguments(NO_SANDBOX);
        options.addArguments(DISABLE_NOTIFICATIONS);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability(CapabilityName.GOOG_LOGGING_PREFS, logPrefs);
        Map<String, Object> perfLogPrefs = new HashMap<>();
        perfLogPrefs.put(ENABLE_NETWORK, true);
        perfLogPrefs.put(TRACE_CATEGORIES, DEVTOOLS_NETWORK);
        options.setExperimentalOption(PERF_LOGGING_PREFS, perfLogPrefs);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
    }
}
