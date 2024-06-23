package org.dorum.automation.common.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.perfecto.CapabilityName;
import org.dorum.automation.perfecto.DriverCapabilities;
import org.dorum.automation.perfecto.PerfectoAPI;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AndroidDriverManager extends AbstractDriverManager {

    private static AppiumDriverLocalService service;
    private static final String SEP = File.separator;
    private static final String APPIUM_JS_LOCATION = "C:" + SEP + "Users" + SEP + "Dorum" + SEP + "AppData" + SEP
            + "Roaming" + SEP + "npm" + SEP + "node_modules" + SEP + "appium" + SEP + "build" + SEP + "lib" + SEP + "main.js";

    @Override
    @Step("Step >> Create Android driver")
    protected AppiumDriver<AndroidElement> createDriver() {
        log.info("Initializing Android driver");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AndroidDriver<AndroidElement> driver = null;
        int counter = 0;
        int maxAttempts = 3;
        while ((driver == null) && (counter < maxAttempts)) {
            try {
                driver = new AndroidDriver<>(getServiceUrl(), capabilities());
            } catch (Exception e) {
                counter++;
                String failure;
                if (StringUtils.isNotEmpty(e.getMessage())
                        && (e.getMessage().contains("Could not find the connected Android device")
                        || e.getMessage().contains("No device was found"))) {
                    failure = "FAILED - find connected device (attempt %s)\n%s";
                } else {
                    log.warn("FAILED - {}", e.getMessage());
                    failure = "FAILED - initialize Android driver (attempt %s)\n%s";
                }
                log.warn(failure, counter, e);
                if (counter == maxAttempts) {
                    log.error(failure, counter, e);
                }
            }
        }
        log.info("Driver initialization time: {} seconds", stopWatch.getTime(TimeUnit.SECONDS));
        stopWatch.stop();
        return driver;
    }

    private static class SingletonHolder {
        public static final AndroidDriverManager INSTANCE = new AndroidDriverManager();
    }

    public static AndroidDriverManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public AppiumDriverLocalService startService() {
        service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .withIPAddress(ConfigProperties.getProperty(ProjectConfig.HOST))
                .usingPort(Integer.parseInt(ConfigProperties.getProperty(ProjectConfig.MOBILE_PORT)))
                .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.ALLOW_INSECURE, Boolean.TRUE.toString())
                .withArgument(GeneralServerFlag.ALLOW_INSECURE, "chromedriver_autodownload")
//                .withArgument(() -> "--allow-insecure","chromedriver_autodownload")
                .withArgument(GeneralServerFlag.LOG_LEVEL, "debug:debug")
                .withArgument(GeneralServerFlag.LOG_TIMESTAMP)
                .withArgument(GeneralServerFlag.DEBUG_LOG_SPACING)
                .withLogFile(getLogFile("AppiumAndroid.log")));
//                .withAppiumJS(new File(APPIUM_JS_LOCATION)));
        service.clearOutPutStreams();
        service.start();
        if (service.isRunning()) {
            log.info("{} driver service is started", ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME));
        }
        return service;
    }

    public void stopService() {
        if ((service != null) && service.isRunning()) {
            service.stop();
            log.info("{} driver service is stopped", ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME));
        } else {
            log.warn("{} driver service is already stopped", ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME));
        }
    }

    @Override
    protected MutableCapabilities capabilities() {
        log.info("Preparing Capabilities");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS))) {
            log.info("Preparing Capabilities (by config)");
            capabilities = DriverCapabilities.perfectoAndroidCapabilities(true, true, false, "", PerfectoAPI.getLatestBuildVersion(true));
        } else {
            log.info("Preparing Capabilities (by static parameters");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "SM-G973F");
            capabilities.setCapability(CapabilityName.APPIUM_ENABLE_WEBVIEW, true);
//            capabilities.setCapability(CapabilityName.APPIUM_SKIP_SERVER_INSTALL, true);
//            capabilities.setCapability(CapabilityName.APPIUM_SYSTEM_PORT, 8200);
            capabilities.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_USE_SYSTEM_EXECUTABLE, true);
            capabilities.setCapability(CapabilityName.ENABLE_MULTI_WINDOWS, true);
            capabilities.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_EXECUTABLE,
                    webDriverManager.getDownloadedDriverPath());
            capabilities.setCapability(AndroidMobileCapabilityType.AUTO_LAUNCH, false);
            capabilities.setCapability(AndroidMobileCapabilityType.ALLOW_TEST_PACKAGES, true);
            capabilities.setCapability(MobileCapabilityType.UDID,
                    ConfigProperties.getProperty(ProjectConfig.ANDROID_DEVICE_ID));
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
            capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 360);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "");
            capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "");
            capabilities.setCapability(AndroidMobileCapabilityType.ENSURE_WEBVIEWS_HAVE_PAGES, true);
        }
        return capabilities;
    }
}
