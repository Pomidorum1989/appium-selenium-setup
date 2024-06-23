package org.dorum.automation.common.driver;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.*;
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

import java.util.concurrent.TimeUnit;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IosDriverManager extends AbstractDriverManager {

    private static AppiumDriverLocalService service;

    @Override
    @Step("Step >> Create iOS Driver")
    protected IOSDriver<IOSElement> createDriver() {
        log.info("Initializing iOS driver");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        IOSDriver<IOSElement> driver = null;
        int counter = 0;
        int maxAttempts = 3;
        while ((driver == null) && (counter < maxAttempts)) {
            try {
                driver = new IOSDriver<>(getServiceUrl(), capabilities());
            } catch (Exception e) {
                counter++;
                String failure;
                if (StringUtils.isNotEmpty(e.getMessage())
                        && e.getMessage().contains("the device was not, or could not be, unlocked")
                        && Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS))) {
                    failure = "FAILED - find connected device (attempt %s)\n%s";
                } else {
                    failure = "FAILED - initialize iOS driver (attempt %s)\n%s";
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

    @Override
    protected MutableCapabilities capabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS))) {
            capabilities = DriverCapabilities.perfectoIosCapabilities(true, true,
                    true, 0, false, "", PerfectoAPI.getLatestBuildVersion(false));
        } else {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "IPhone 11 Pro");
            capabilities.setCapability(MobileCapabilityType.UDID, ConfigProperties.getProperty(ProjectConfig.IOS_DEVICE_ID));
            capabilities.setCapability(CapabilityName.APPIUM_ENABLE_WEBVIEW, true);
//            capabilities.setCapability(CHROMEDRIVER_USE_SYSTEM_EXECUTABLE, true);
//            capabilities.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_EXECUTABLE,"");
//            capabilities.setCapability(AndroidMobileCapabilityType.CHROMEDRIVER_EXECUTABLE, webDriverManager.getDownloadedDriverPath());
//            capabilities.setCapability(CapabilityName.BUNDLE_ID, GlobalEnvProperties.brandGM.getValidation());
//            capabilities.setCapability(IOSMobileCapabilityType.USE_NEW_WDA, false);
//            capabilities.setCapability(MobileCapabilityType.APP, "");
//            capabilities.setCapability(CapabilityName.UPDATE_WDA_BUNDLE_ID, "com.gm.IntegrationApp");
            capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "");
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
            capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1000);
            capabilities.setCapability(AndroidMobileCapabilityType.AUTO_LAUNCH, false);
            capabilities.setCapability(IOSMobileCapabilityType.XCODE_ORG_ID, "");
            capabilities.setCapability(IOSMobileCapabilityType.XCODE_SIGNING_ID, "Apple Development");
            capabilities.setCapability(IOSMobileCapabilityType.SHOW_XCODE_LOG, true);
            capabilities.setCapability(IOSMobileCapabilityType.USE_PREBUILT_WDA, true);
        }
        return capabilities;
    }

    public AppiumDriverLocalService startService() {
        service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .withIPAddress(ConfigProperties.getProperty(ProjectConfig.HOST))
                .usingPort(Integer.parseInt(ConfigProperties.getProperty(ProjectConfig.MOBILE_PORT)))
                .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.ALLOW_INSECURE, Boolean.TRUE.toString())
                .withArgument(GeneralServerFlag.LOG_LEVEL, "debug:debug")
                .withArgument(GeneralServerFlag.LOG_TIMESTAMP)
                .withArgument(GeneralServerFlag.DEBUG_LOG_SPACING)
                .withLogFile(getLogFile("AppiumIos.log")));
        service.clearOutPutStreams();
        service.start();
        if (service.isRunning()) {
            log.info("%s driver service is started", ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME));
        }
        return service;
    }

    public void stopService() {
        if ((null != service) && service.isRunning()) {
            service.stop();
            log.info("Webdriver service for IOS is stopped");
        }
    }

    private static class SingletonHolder {
        public static final IosDriverManager INSTANCE = new IosDriverManager();
    }

    public static IosDriverManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
