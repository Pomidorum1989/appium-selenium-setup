package org.dorum.automation.common.utils.perfecto;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.dorum.automation.common.consts.CapabilityName;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.driver.DriverType;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DriverCapabilities {

    public static final DesiredCapabilities capabilities = new DesiredCapabilities();

    public static void perfectoGeneralCapabilities(String buildLocation, boolean isSensor) {
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.APPIUM);
        capabilities.setCapability(CapabilityName.APPIUM_VERSION, "1.22.3");
        capabilities.setCapability(CapabilityName.SECURITY_TOKEN, PerfectoCommands.getPerfectoToken());
        capabilities.setCapability(MobileCapabilityType.APP, buildLocation);
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.RUN_BY_JENKINS))
                && ConfigProperties.getProperty(ProjectConfig.SESSION_ID).isEmpty()) { // Jenkins execution
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, true); // corresponds to phone
            capabilities.setCapability(MobileCapabilityType.NO_RESET, false); // corresponds to app
            if (AbstractDriverManager.isAndroid()) {
                capabilities.setCapability(MobileCapabilityType.NO_RESET, true); // corresponds to app
            }
            capabilities.setCapability(CapabilityName.DATA_RESET, true); // corresponds to app
        } else {                                                                // Local execution (without Setup)
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, false); // corresponds to phone
            capabilities.setCapability(MobileCapabilityType.NO_RESET, true); // corresponds to app
            capabilities.setCapability(CapabilityName.DATA_RESET, false); // corresponds to app
        }
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1000);
        capabilities.setCapability(MobileCapabilityType.ORIENTATION, "PORTRAIT");
        capabilities.setCapability(CapabilityName.TAKES_SCREENSHOT, false);
        capabilities.setCapability(CapabilityName.SCREENSHOT_ON_ERROR, true);
        capabilities.setCapability(CapabilityName.AUTO_LAUNCH, true);
        if (isSensor) {
            capabilities.setCapability(CapabilityName.SENSOR_INSTRUMENT, true);
        }
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_EXACT_DEVICE))) {
            String udId = ConfigProperties.getProperty(ProjectConfig.ANDROID_DEVICE_ID);
            if (AbstractDriverManager.isIos()) {
                udId = ConfigProperties.getProperty(ProjectConfig.IOS_DEVICE_ID);
            }
            capabilities.setCapability(MobileCapabilityType.UDID, udId);
            Log.info("Device ID is set to: %s", udId);
            if (!ConfigProperties.getProperty(ProjectConfig.SESSION_ID).isEmpty()) {
                capabilities.setCapability(CapabilityName.DEVICE_SESSION_ID,
                                           ConfigProperties.getProperty(ProjectConfig.SESSION_ID));
                Log.info("Session ID is set to: %s",
                         ConfigProperties.getProperty(ProjectConfig.SESSION_ID));
            }
        } else {
            searchForDevice(true);
        }
    }

    private static void searchForDevice(boolean isApiSearch) {
        String platformName, platformVersion, manufacturer, model;
        if (isApiSearch) {
            capabilities.setCapability(MobileCapabilityType.UDID, PerfectoAPI.getAvailableDevice());
        } else {
            if (AbstractDriverManager.isAndroid()) {
                platformName = DriverType.ANDROID.getName();
                platformVersion = "[1][0-3]";
                manufacturer = "Samsung|Google";
                model = "Galaxy S.*|Galaxy N.*|Galaxy Z.*|Pixel";
            } else {
                platformName = DriverType.IOS.getName().toLowerCase();
                platformVersion = "^[1][4-6].*";
                manufacturer = "Apple";
                model = "iPhone-1.*(?!Mini)";
            }
            capabilities.setCapability(CapabilityName.OPEN_DEVICE_TIMEOUT, 1);
            capabilities.setCapability(CapabilityName.PLATFORM_NAME, platformName);
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
            capabilities.setCapability(CapabilityName.MANUFACTURER, manufacturer);
            capabilities.setCapability(CapabilityName.MODEL, model);
        }
    }

    public static DesiredCapabilities perfectoIosCapabilities(
            boolean isNewArchitecture, boolean isAcceptAlerts, boolean isHybrid, int childrenAmount, boolean isSensor,
            String buildLocation) {
        perfectoGeneralCapabilities(buildLocation, isSensor);
        capabilities.setCapability(CapabilityName.SCRIPT_NAME, "script name"
                + ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION));
        capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "bundle id");
        capabilities.setCapability(MobileCapabilityType.LANGUAGE, "en");
        capabilities.setCapability(MobileCapabilityType.LOCALE, "en_US");
        if (isAcceptAlerts) {
            capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true);
        } else {
            capabilities.setCapability(IOSMobileCapabilityType.AUTO_DISMISS_ALERTS, true);
        }
        if (isNewArchitecture) {
            if (isHybrid) {
                capabilities.setCapability(CapabilityName.FULL_CONTEXT_LIST, true);
                capabilities.setCapability(CapabilityName.IOS_RESIGN, true);
                capabilities.setCapability(CapabilityName.USE_APPIUM_FOR_HYBRID, true);
                if (childrenAmount > 0) {
                    capabilities.setCapability(CapabilityName.MAX_CHILDREN, childrenAmount);
                }
            } else {
                capabilities.setCapability(CapabilityName.USE_APPIUM_FOR_WEB, true);
                capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.SAFARI);
            }
        } else {
            capabilities.setCapability(CapabilityName.AUTO_INSTRUMENT, true);
        }
        return capabilities;
    }

    public static DesiredCapabilities perfectoAndroidCapabilities(
            boolean isNewArchitecture, boolean isHybrid, boolean isSensor, String buildLocation) {
        Log.info("Preparing Android Capabilities");
        capabilities.setCapability(CapabilityName.SCRIPT_NAME, "");
        capabilities.setCapability(AndroidMobileCapabilityType.DEVICE_READY_TIMEOUT, 5000);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "");
        capabilities.setCapability(AndroidMobileCapabilityType.ENFORCE_APP_INSTALL, true);
        capabilities.setCapability(AndroidMobileCapabilityType.UNINSTALL_OTHER_PACKAGES, "");
//        capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_PACKAGE, NOMAD_VALIDATION);
//        capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, MY_BRAND_MAIN_ACTIVITY);
//        capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_DURATION, 1);
        perfectoGeneralCapabilities(buildLocation, isSensor);
        if (isNewArchitecture) {
            if (isHybrid) {
                capabilities.setCapability(CapabilityName.ENABLE_APPIUM_BEHAVIOR, true);
                capabilities.setCapability(CapabilityName.USE_APPIUM_FOR_HYBRID, true);
            } else {
                capabilities.setCapability(CapabilityName.USE_APPIUM_FOR_WEB, true);
                capabilities.setCapability(CapabilityType.BROWSER_NAME, DriverType.CHROME.getName());
            }
        } else {
            capabilities.setCapability(CapabilityName.AUTO_INSTRUMENT, true);
        }
        return capabilities;
    }
}
