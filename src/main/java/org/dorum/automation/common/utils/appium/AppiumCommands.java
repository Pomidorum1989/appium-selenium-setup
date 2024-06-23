package org.dorum.automation.common.utils.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.offset.PointOption;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.driver.WebDriverContainer;
import org.dorum.automation.common.utils.*;
import org.dorum.automation.common.utils.enums.MobileContext;
import org.dorum.automation.common.utils.enums.PerformanceData;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.common.utils.enums.TitleName;
import org.dorum.automation.perfecto.CapabilityName;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.dorum.automation.common.utils.MathUtils.trimDecimal;

@Log4j2
public class AppiumCommands {

    private static final List<String[]> PERFORMANCE_DATA = new ArrayList<>();

    static {
        PERFORMANCE_DATA.add(new String[]{
                "   CPU-User(%)   ", "   CPU-Kernel(%)   ", "   Memory(kb)   ", "   Network Rx(kb)   ",
                "   Network Tx(kb)   ", "   Battery(%)   ", "   Time   "});
    }

    public static void activateApp(String bundlePackageId) {
        log.info("Appium Commands: activating application: {}", bundlePackageId);
        try {
            appiumDriver().activateApp(bundlePackageId);
        } catch (Exception e) {
            log.warn("FAILED - unable to activate package/bungle ID: {}", bundlePackageId, e);
        }
    }

    public static boolean isApplicationInstalled(String bundleId) {
        if (appiumDriver().isAppInstalled(bundleId)) {
            log.info("{} is already installed", bundleId);
        }
        return false;
    }

    @Step("Step >> Appium Commands: Terminate Application")
    public static void terminateApp(String bundleId) {
        try {
            log.info("Appium Commands: terminating application");
            if (appiumDriver().terminateApp(bundleId)) {
                log.info("Application with package/bungle ID: '{}' is terminated", bundleId);
            }
        } catch (Exception e) {
            log.warn("FAILED - unable to terminate package/bungle ID: {}", bundleId, e);
        }
    }

    @Step("Step >> Appium Commands: Start Activity")
    public static void startActivity(String packageId, String activity) {
        try {
            log.info("Appium Commands: starting activity '{}' for package '{}'", activity, packageId);
            androidDriver().startActivity(new Activity(packageId, activity));
            log.info("Activity {} is started", activity);
        } catch (Exception e) {
            log.error("FAILED - unable to start activity '{}' in the package: {}\n{}", activity, packageId, e);
        }
    }

    public static void startActivityWithWait(String packageId, String activity) {
        log.info("Appium Commands: starting activity (with wait)");
        androidDriver().startActivity(
                new Activity(packageId, activity)
                        .setAppWaitPackage(packageId)
                        .setAppWaitActivity(activity));
        log.info("Activity with wait {} is started", activity);
    }

    @Step("Step >> Appium Commands: Launch Application")
    public static void launchApp() {
        if (AppiumCommands.isCapabilityActive(CapabilityName.APP_PACKAGE)
                || AppiumCommands.isCapabilityActive(CapabilityName.BUNDLE_ID)) {
            try {
                log.info("Appium Commands: launching application");
                appiumDriver().launchApp();
                log.info("Launched the application under the test");
            } catch (Exception e) {
                log.error("FAILED - unable to launch the application\n{}", e);
            }
        } else {
            activateApp("packageName");
        }
    }

    @Step("Step >> Appium Commands: Close Application")
    public static void closeApp() {
        try {
            log.info("Appium Commands: closing application");
            appiumDriver().closeApp();
            log.info("Closed the application under the test");
        } catch (Exception e) {
            log.warn("FAILED - unable to close the application\n{}", e);
        }
    }

    @Step("Step >> Appium Commands: Remove Application")
    public static void removeApp(String appOrBundleId) {
        try {
            log.info("Appium Commands: removing application {}", appOrBundleId);
            appiumDriver().removeApp(appOrBundleId);
            log.info("Removed the application ({}) under the test", appOrBundleId);
        } catch (Exception e) {
            log.warn("FAILED - unable to remove the application ({})\n{}", appOrBundleId, e);
        }
    }

    @Step("Step >> Appium Commands: Reset Application")
    public static void resetApp() { // Also clears cache and all data
        try {
            log.info("Appium Commands: resetting application");
            appiumDriver().resetApp();
            log.info("Done - reset the application under the test");
        } catch (Exception e) {
            log.warn("FAILED - unable to reset the application\n{}", e);
        }
    }

    public static boolean isCapabilityActive(String capability) {
        return appiumDriver().getCapabilities().getCapabilityNames().contains(capability);
    }

    public static String getDesiredCapabilityValue(String capability) {
        return (String) ((Map<String, ?>) getCapability("desired")).get(capability);
    }

    public static Object getCapability(String capability) {
        return appiumDriver().getCapabilities().getCapability(capability);
    }

    public static String getSessionId() {
        SessionId id = appiumDriver().getSessionId();
        if (id != null) {
            return id.toString();
        }
        return "";
    }

    public static String getPlatformName() {
        return appiumDriver().getPlatformName();
    }

    public static String getCurrentActivity() {
        String currentActivity = androidDriver().currentActivity();
        log.info("Current activity: {}", currentActivity);
        return currentActivity;
    }

    public static boolean waitForActivityIsLoaded(long timeoutMilliSeconds, String activity) {
        log.info("Appium Commands: waiting for activity '{}' load", activity);
        boolean isLoaded = false;
        if (AbstractDriverManager.isAndroid()) {
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < timeoutMilliSeconds) {
                if (Objects.equals(getCurrentActivity(), activity)) {
                    isLoaded = true;
                    log.info("{} was loaded", activity);
                    break;
                }
            }
        } else {
            log.warn("The command is not supported by iOS");
        }
        return isLoaded;
    }

    public static Set<String> getContextHandles() {
        log.info("Appium Commands: getting context handles");
        Set<String> handles = new LinkedHashSet<>();
        int attempt = 0;
        while (attempt < 2) {
            try {
                handles = appiumDriver().getContextHandles();
                break;
            } catch (Exception e) {
                attempt++;
                log.warn("FAILED - unable to get context handles\n{}", e);
            }
        }
        return handles;
    }

    public static boolean setContext(MobileContext context) {
        log.info("Appium Commands: setting context");
        String contextValue = context.getContextValue();
        if (context.equals(MobileContext.WEBVIEW) && AbstractDriverManager.isIos()) {
            contextValue = getIosWebViewContext(TitleName.APP_TITLE);
            if (contextValue == null) {
                log.warn("FAILED - get iOS WebView context: NULL");
                return false;
            }
        }
        try {
            appiumDriver().context(contextValue);
        } catch (Exception e) {
            log.warn("FAILED - set context\n{}", e);
            return false;
        }
        log.info("Switched context, current context is {}", appiumDriver().getContext());
        return true;
    }

    public static String getCurrentContext() {
        String context = "";
        try {
            log.info("Appium Commands: getting current context");
            context = appiumDriver().getContext();
            log.info("Current context is: {}", context);
        } catch (Exception e) {
            log.warn("FAILED - unable to get current context\n{}", e);
        }
        return context;
    }

    public static String getWebViewTitleName() {
        log.info("Appium Commands: getting webView title");
        String result = "";
        int attempt = 0;
        while (attempt < 1) {
            try {
                result = WebDriverContainer.getDriver().getTitle();
                if (!result.isEmpty()) {
                    log.info("Current webView title is: {}", result);
                } else {
                    log.warn("Title name is empty");
                }
                break;
            } catch (Exception e) {
                attempt++;
                log.warn("WebView title name is not found\n{}", e);
            }
        }
        return result;
    }

    public static Set<String> getWindowHandles() {
        Set<String> result = new HashSet<>();
        try {
            log.info("Appium Commands: getting window handles");
            result = WebDriverContainer.getDriver().getWindowHandles();
//            result.forEach(handle -> log.info("Available handles: {}", handle));
        } catch (Exception e) {
            log.warn("Windows handles are not found\n{}", e);
        }
        return result;
    }

    @Step("Step >> Appium Commands: Switch Context (if relevant)")
    public static boolean switchContext(MobileContext context) {
        log.info("Appium Commands: switching context (if relevant) to ({})", context);
        try {
            return setContext(context);
        } catch (Exception e) {
            if (e.getMessage().contains("Chrome version")) {
                log.warn("ChromeDriver update is needed\n{}", e);
                return false;
            } else {
                log.warn("No such context found\n{}", e);
                return false;
            }
        }
    }

    @SneakyThrows
    @Step("Step >> Appium Commands: Switch Context (if relevant) by title")
    public static boolean switchContextUsingTitle(TitleName title) {
        log.info("Appium Commands: switching context (if relevant) by title {}", title);
        if (getCurrentContext().contains(MobileContext.WEBVIEW.getContextName() + "_")
                && Objects.equals(getWebViewTitleName(), title.getTitle())) {
                log.info("The handle - {} is already set", title.getTitle());
                return true;
        } else {
            if (switchContext(MobileContext.WEBVIEW)) {
                for (String windowHandle : getWindowHandles()) {
                    try {
                        switchToWindow(windowHandle);
                        if (getWebViewTitleName().equals(title.getTitle())) {
                            log.info("The handle - {} is found", title.getTitle());
                            return true;
                        }
                    } catch (Exception e) {
                        log.warn("Window is not found\n{}", e);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Step("Step >> Appium Commands: Switch context by title")
    public static boolean switchContextByTitle(TitleName title) {
        log.info("Appium Commands: switching context by title {})", title);
        if (!AbstractDriverManager.isAndroid()
                && Objects.equals(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS), Boolean.TRUE.toString())
                && isCapabilityActive(CapabilityName.FULL_CONTEXT_LIST)) {
            return getIosWebViewContext(title) != null;
        } else {
            if (ConfigProperties.getProperty(ProjectConfig.SESSION_ID) == null ||
                    ConfigProperties.getProperty(ProjectConfig.SESSION_ID).isEmpty()) {
                return switchContextUsingTitle(title);
            } else {
                return switchContext(MobileContext.WEBVIEW_DEBUG);
            }
        }
    }

    @SneakyThrows
    public static String getIosWebViewContext(TitleName titleName) {
        log.info("Appium Commands: getting iOS context");
        for (Object contextHandle : getContextHandles()) {
            if (contextHandle instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) contextHandle;
                if (map.containsKey("id") && map.containsKey("title")) {
                    String id = (String) map.get("id");
                    String title = (String) map.get("title");
                    if (id.contains(MobileContext.WEBVIEW.getContextName()) && Objects.equals(title, titleName.getTitle())) {
                        return id;
                    }
                }
            }
        }
        log.warn("FAILED - get iOS WebView context: {}", titleName);
        return null;
    }

    public static void switchToWindow(String windowHandle) {
        log.info("Appium Commands: setting window context");
        WebDriverContainer.getDriver().switchTo().window(windowHandle);
        log.info("Switched to: {}", windowHandle);
    }

    public static void switchIFrameByWebElement(WebElement webElement) {
        switchToParentFrame();
        WebDriverContainer.getDriver().switchTo().frame(webElement);
    }

    public static void switchToParentFrame() {
        WebDriverContainer.getDriver().switchTo().parentFrame();
    }

    public static Dimension getScreenSize() {
        log.info("Appium Commands: getting screen size");
        return WebDriverContainer.getDriver().manage().window().getSize();
    }

    public static String getPageSource() {
        return WebDriverContainer.getDriver().getPageSource();
    }

    public static void hideKeyBoard() {
        log.info("Appium Commands: hiding keyboard");
        switchContext(MobileContext.NATIVE);
        appiumDriver().hideKeyboard();
        log.info("Keyboard is hidden");
    }

    public static void setScriptTimeout(int seconds) {
        appiumDriver().manage().timeouts().setScriptTimeout(seconds, TimeUnit.SECONDS);
    }

    public static WebElement scrollIntoView(String visibleText) {
        WebElement element = null;
        try {
            // NOTE: findElementByAndroidUIAutomator - working with double quotes >> "<visibleText>"!!!
            String locator = String.format("new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                    + ".scrollIntoView(new UiSelector().textContains(\"%s\").instance(0))", visibleText);
            log.info("Appium Commands: Scrolling to visible text: {}\nLocator: {}", visibleText, locator);
            element = (androidDriver()).findElementByAndroidUIAutomator(locator);
            log.info("Scrolled to visible text: {}", visibleText);
        } catch (Exception e) {
            log.warn("Visible text is not found: {}\n{}", visibleText, e);
        }
        return element;
    }

    public void scrollAndClick(String visibleText) {
        scrollIntoView(visibleText).click();
    }

    @SneakyThrows
    public static void tapByCoordinates(int x, int y) {
        log.info("Appium Commands: taping by coordinates - x:{} y:{}", x, y);
        new TouchAction<>(appiumDriver()).tap(PointOption.point(x, y)).perform();
        log.info("Tapped on coordinates - x:{} y:{}", x, y);
    }

    @SneakyThrows
    public static void tapByCoordinates(int x, int y, int times) {
        TouchAction<?> touchAction = new TouchAction<>(appiumDriver());
        for (int i = 0; i < times; i++) {
            touchAction.tap(PointOption.point(x, y));
        }
        touchAction.perform();
        log.info("Tapped on coordinates - x:{} y:{}", x, y);
    }

    public static void tapByCoordinatesInSequence(List<Integer> taps) {
        log.info("Appium Commands: taping by coordinates in sequence");
        TouchAction<?> touchAction = new TouchAction<>(androidDriver());
        if (taps.size() % 2 == 0) {
            for (int i = 0; i < taps.size() - 1; i = i + 2) {
                touchAction.tap(PointOption.point(taps.get(i), taps.get(i + 1)));
                log.info("Will tap on coordinates - x:{} y:{}", taps.get(i), taps.get(i + 1));
            }
            touchAction.perform();
            log.info("Tapped on listed coordinates");
        } else {
            log.warn("Incorrect amount of taps: {}", taps.size());
        }
    }

    public static void tapImmediatelyByCoordinatesInSequence(List<Integer> taps) {
        log.info("Appium Commands: taping immediately by coordinates in sequence");
        if (taps.size() % 2 == 0) {
            for (int i = 0; i < taps.size() - 1; i = i + 2) {
                tapByCoordinates(taps.get(i), taps.get(i + 1));
            }
        } else {
            log.warn("Incorrect amount of taps: {}", taps.size());
        }
    }

    @SneakyThrows
    public static void tapCenterOfScreen() {
        log.info("Appium Commands: taping center of screen");
        int x = AppiumCommands.getScreenSize().getWidth() / 2;
        int y = AppiumCommands.getScreenSize().getHeight() / 2;
        new TouchAction<>(appiumDriver()).tap(PointOption.point(x, y)).perform();
        log.info("Tapped center of screen (coordinates - x:{} y:{})", x, y);
    }

    public void swipeByCoordinates(int x, int y) {
        TouchAction<?> touchAction = new TouchAction<>(appiumDriver());
        touchAction
                .press(PointOption.point(x, y))
                .moveTo(PointOption.point(x, y))
                .release();
        log.info("Swiped on coordinates - x:{} y:{}", x, y);
    }

    public static File takeScreenshot(String downloadsFolder, String fileName) {
        log.info("Appium Commands: taking screenshot");
        String sep = File.separator;
        String screenshotFileName = (fileName + ".png")
                .replace(" ", "_")
                .replace("'", "")
                .replace(":", "");
        File imageFile = new File("");
        try {
            File scrFile = ((TakesScreenshot) WebDriverContainer.getDriver()).getScreenshotAs(OutputType.FILE);
            imageFile = new File(downloadsFolder, screenshotFileName);
//            Utils.compressImage(scrFile.getAbsolutePath(), imageFile.getAbsolutePath(), 0.0f);
            FileUtils.copyFile(scrFile, imageFile);
            String path = TextUtils.format(" {0}{1}{2}{1}{3}", System.getProperty("user.dir"),
                    sep, downloadsFolder.replace("/", sep), screenshotFileName);
            log.info("Screenshot is created in:\n{}", path);
        } catch (Exception e) {
            log.warn("FAILED - take screenshot: {}\n{}", screenshotFileName, e);
        }
        return imageFile;
    }

    public static void clickAndroidSystemBtn(AndroidKey key, String comment) {
        log.info("Appium Commands: clicking Android system button {}", key);
        switchContext(MobileContext.NATIVE);
        try {
            androidDriver().pressKey(new KeyEvent(key));
            log.info(comment);
        } catch (Exception e) {
            log.warn("FAILED - unable to activate system key: {}\n{}", key.name(), e);
        }
    }

    private static List<List<Object>> getPerformanceData(PerformanceData.PerformanceDataType types, int dataReadTimeout) {
        log.info("Appium Commands: getting performance data");
        List<List<Object>> data = new ArrayList<>();
        try {
            data = androidDriver().getPerformanceData("app package", types.getValue(), dataReadTimeout);
        } catch (Exception e) {
            log.warn("FAILED - unable to get {} data\n{}", types.getValue(), e);
        }
        return data;
    }

    public static HashMap<String, Double> getCPUData() {
        List<List<Object>> data = getPerformanceData(PerformanceData.PerformanceDataType.CPU_INFO, 3);
        return getDataAsHashMap(data);
    }

    public static HashMap<String, Double> getMemoryData() {
        List<List<Object>> data = getPerformanceData(PerformanceData.PerformanceDataType.MEMORY_INfO, 3);
        return getDataAsHashMap(data);
    }

    public static HashMap<String, Double> getNetworkData() {
        List<List<Object>> data = getPerformanceData(PerformanceData.PerformanceDataType.NETWORK_INFO, 3);
        return getDataAsHashMap(data);
    }

    public static HashMap<String, Double> getBatteryData() {
        List<List<Object>> data = getPerformanceData(PerformanceData.PerformanceDataType.BATTERY_INFO, 3);
        return getDataAsHashMap(data);
    }

    public static void navigateBack() {
        log.info("Appium Commands: executing 'Back' action by Android Driver (Selenium level)");
        WebDriverContainer.getDriver().navigate().back();
    }

    public static void acceptAlert() {
        WebDriverContainer.getDriver().switchTo().alert().accept();
    }

    public static ApplicationState queryAppState(String bundleID) {
        log.info("Appium Commands: querying application state");
        return appiumDriver().queryAppState(bundleID);
    }

    @Step("Step >> Appium Commands: Record Performance data")
    public static void recordPerformanceData() {
        log.info("Appium Commands: recording performance data results");
        String context = getCurrentContext();
        switchContext(MobileContext.NATIVE);
        HashMap<String, Double> infoMemory = getMemoryData();
        HashMap<String, Double> infoNetwork = getNetworkData();
        HashMap<String, Double> infoBattery = getBatteryData();
        HashMap<String, Double> infoCPU = getCPUData();
        if (context.equals(MobileContext.WEBVIEW.getContextValue())) {
            switchContextByTitle(TitleName.APP_TITLE);
        }
        Double cpuUserValue = 0.0, cpuKernelValue = 0.0, memoryValue = 0.0, networkRbValue = 0.0, networkTbValue = 0.0,
                batteryValue = 0.0;
        try {
            cpuUserValue = infoCPU.get(PerformanceData.CPUData.USER.getValue());
            cpuKernelValue = infoCPU.get(PerformanceData.CPUData.KERNEL.getValue());
            memoryValue = infoMemory.get(PerformanceData.MemoryData.TOTAL_PSS.getValue());
            networkRbValue = infoNetwork.get(PerformanceData.NetworkData.RB.getValue());
            networkTbValue = infoNetwork.get(PerformanceData.NetworkData.TB.getValue());
            batteryValue = infoBattery.get(PerformanceData.BatteryData.POWER.getValue());
        } catch (Exception e) {
            log.warn("FAILED - unable to record performance data\n", e);
        }
        if (cpuUserValue == null) cpuUserValue = 0.0;
        if (cpuKernelValue == null) cpuKernelValue = 0.0;
        if (memoryValue == null) memoryValue = 0.0;
        if (networkRbValue == null) networkRbValue = 0.0;
        if (networkTbValue == null) networkTbValue = 0.0;
        if (batteryValue == null) batteryValue = 0.0;
        PERFORMANCE_DATA.add(new String[]{
                trimDecimal(cpuUserValue), trimDecimal(cpuKernelValue), trimDecimal(memoryValue),
                trimDecimal(networkRbValue), trimDecimal(networkTbValue), trimDecimal(batteryValue),
                new Date().toString()});
        log.info("Performance data -> | CPU(User): {}%  | CPU(Kernel): {}%  | Memory: {} kb"
                        + " | Network Rx: {} kb | Network Tx: {} kb | Battery: {}%  |",
                trimDecimal(cpuUserValue), trimDecimal(cpuKernelValue), trimDecimal(memoryValue),
                trimDecimal(networkRbValue), trimDecimal(networkTbValue), trimDecimal(batteryValue));
    }

    @SneakyThrows
    @Step("Step >> Appium Commands: Save Performance data results")
    public static void stopCpuDataRecording(String testName) {
        log.info("Appium Commands: finishing CPU metrics recording (and save results) for test {}", testName);
        File file = DataUtils.writeToCSV(PERFORMANCE_DATA);
        CustomSoftAssert.addAttachmentToAllure("perf_log_" + testName, file.toPath());
        FileUtils.copyFile(file, new File(String.format("log location", "perf_log_") + testName + ".csv"));
        checkPerformance();
        PERFORMANCE_DATA.clear();
    }

    public static File pullFile(String remotePath, String internalPath) {
        byte[] bytes = androidDriver().pullFile(remotePath);
        log.info("Downloaded file from {}", remotePath);
        return DataUtils.creteFileFromBytes(bytes, internalPath);
    }

    public static void pushFile(String remotePath, File file) {
        try {
            androidDriver().pushFile(remotePath, file);
            log.info("Pushed file to {}", remotePath);
        } catch (Exception e) {
            log.warn("FAILED - unable to push the file {}", remotePath, e);
        }
    }

    //--------------- Private Methods ---------------

    private static AppiumDriver<?> appiumDriver() {
        return ((AppiumDriver<?>) eventFiringWebDriver());
    }

    private static AndroidDriver<?> androidDriver() {
        return ((AndroidDriver<?>) eventFiringWebDriver());
    }

    private static IOSDriver<?> iosDriver() {
        return ((IOSDriver<?>) eventFiringWebDriver());
    }

    private static WebDriver eventFiringWebDriver() {
        return ((EventFiringWebDriver) WebDriverContainer.getDriver()).getWrappedDriver();
    }

    private static HashMap<String, Double> getDataAsHashMap(List<List<Object>> data) {
        HashMap<String, Double> readableData = new HashMap<>();
        try {
            for (int i = 0; i < data.get(0).size(); i++) {
                double val;
                if (data.get(1).get(i) == null) {
                    val = 0;
                } else {
                    val = Double.parseDouble((String) data.get(1).get(i));
                }
                readableData.put((String) data.get(0).get(i), val);
            }
        } catch (Exception e) {
            log.warn("FAILED - unable to record performance info\n", e);
        }
        return readableData;
    }

    private static void checkPerformance() {
        double initialCPUUserValue = 0.0, initialCPUKernelValue = 0.0, initialMemoryValue = 0.0,
                finalCPUUserValue = 0.0, finalCPUKernelValue = 0.0, finalMemoryValue = 0.0;
        try {
            initialCPUUserValue = Double.parseDouble(PERFORMANCE_DATA.get(1)[0]);
            initialCPUKernelValue = Double.parseDouble(PERFORMANCE_DATA.get(1)[1]);
            initialMemoryValue = Double.parseDouble(PERFORMANCE_DATA.get(1)[2]);
        } catch (Exception e) {
            log.warn("FAILED - unable to read initial performance data\n{}", e);
        }
        try {
            finalCPUUserValue = Double.parseDouble(PERFORMANCE_DATA.get(PERFORMANCE_DATA.size() - 1)[0]);
            finalCPUKernelValue = Double.parseDouble(PERFORMANCE_DATA.get(PERFORMANCE_DATA.size() - 1)[1]);
            finalMemoryValue = Double.parseDouble(PERFORMANCE_DATA.get(PERFORMANCE_DATA.size() - 1)[2]);
        } catch (Exception e) {
            log.warn("FAILED - unable to read final performance data\n{}", e);
        }
        if (initialCPUUserValue == 0.0) initialCPUUserValue = 1.0;
        if (initialCPUKernelValue == 0.0) initialCPUKernelValue = 1.0;
        if (initialMemoryValue == 0.0) initialMemoryValue = 1.0;
        double increasedCPUUser = (finalCPUUserValue * 100) / initialCPUUserValue;
        double increasedCPUKernel = (finalCPUKernelValue * 100) / initialCPUKernelValue;
        double increasedMemory = (finalMemoryValue * 100) / initialMemoryValue;
        log.info("Increase rate -> CPU-User:{} percent, CPU-Kernel:{} percent, Memory:{} percent",
                trimDecimal(increasedCPUUser, 1), trimDecimal(increasedCPUKernel, 1),
                trimDecimal(increasedMemory, 1));
        if (increasedMemory >= 10.0) {
            log.warn("Initial memory value: {}, final memory value: {}",
                    trimDecimal(initialMemoryValue, 1), trimDecimal(finalMemoryValue, 1));
            log.warn("Increased value: {}, memory increased more than on 10 percent",
                    trimDecimal(increasedMemory, 1));
        }
        if (increasedCPUUser >= 20.0) {
            log.warn("Initial CPU-User value: {}, final CPU-User value: {}",
                    trimDecimal(initialCPUUserValue, 1), trimDecimal(finalCPUUserValue, 1));
            log.warn("Increased value: {}, CPU-User increased more than on 10 percent",
                    trimDecimal(increasedCPUUser));
        }
        if (increasedCPUKernel >= 20.0) {
            log.warn("Initial CPU-Kernel value: {}, final CPU-Kernel value: {}",
                    trimDecimal(initialCPUKernelValue, 1), trimDecimal(finalCPUKernelValue, 1));
            log.warn("Increased value: {}, CPU-Kernel increased more than on 10 percent",
                    trimDecimal(increasedCPUKernel, 1));
        }
    }
}
