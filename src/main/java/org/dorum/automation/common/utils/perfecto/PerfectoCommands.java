package org.dorum.automation.common.utils.perfecto;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.driver.WebDriverContainer;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.DataUtils;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.TextUtils;
import org.dorum.automation.common.utils.enums.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.*;

@NoArgsConstructor
public class PerfectoCommands {

    private static final String APP = "application";
    private static final String BTN = "button";
    private static final String CLEAN = "clean";
    private static final String CLICK = "click";
    private static final String DEVICE = "device";
    private static final String FIND = "find";
    private static final String IMG = "image";
    private static final String LOCATION = "location";
    private static final String LOGS = "logs";
    private static final String MOBILE = "mobile";
    private static final String MONITOR = "monitor";
    private static final String OBJ_OPT = "objects.optimization";
    private static final String OPEN = "open";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String TOUCH = "touch";
    private static final String TXT = "text";
    private static final String VIRTUALIZATION = "vnetwork";
    private static final String MOBILE_ACTIVITY_OPEN = String.format("%s:activity:%s", MOBILE, OPEN);
    private static final String MOBILE_ACTIVITY_SYNC = String.format("%s:activity:sync", MOBILE);
    private static final String MOBILE_APP_CLEAN = String.format("%s:%s:%s", MOBILE, APP, CLEAN);
    private static final String MOBILE_APP_CLOSE = String.format("%s:%s:close", MOBILE, APP);
    private static final String MOBILE_APP_FIND = String.format("%s:%s:%s", MOBILE, APP, FIND);
    private static final String MOBILE_APP_INSTALL = String.format("%s:%s:install", MOBILE, APP);
    private static final String MOBILE_APP_OPEN = String.format("%s:%s:%s", MOBILE, APP, OPEN);
    private static final String MOBILE_APP_UNINSTALL = String.format("%s:%s:uninstall", MOBILE, APP);
    private static final String MOBILE_BTN_IMG_CLICK = String.format("%s:%s-%s:%s", MOBILE, BTN, IMG, CLICK);
    private static final String MOBILE_BTN_TXT_CLICK = String.format("%s:%s-%s:%s", MOBILE, BTN, TXT, CLICK);
    private static final String MOBILE_DEVICE_INFO = String.format("%s:%s:info", MOBILE, DEVICE);
    private static final String MOBILE_DEVICE_LOG = String.format("%s:%s:log", MOBILE, DEVICE);
    private static final String MOBILE_DEVICE_ROTATE = String.format("%s:%s:rotate", MOBILE, DEVICE);
    private static final String MOBILE_GET_LOCATION = String.format("%s:%s:get", MOBILE, LOCATION);
    private static final String MOBILE_HANDSET_SHELL = String.format("%s:handset:shell", MOBILE);
    private static final String MOBILE_IMG_FIND = String.format("%s:%s:%s", MOBILE, IMG, FIND);
    private static final String MOBILE_KEY_EVENT = String.format("%s:key:event", MOBILE);
    private static final String MOBILE_LOGS_START = String.format("%s:%s:%s", MOBILE, LOGS, START);
    private static final String MOBILE_LOGS_STOP = String.format("%s:%s:%s", MOBILE, LOGS, STOP);
    private static final String MOBILE_MONITOR_START = String.format("%s:%s:%s", MOBILE, MONITOR, START);
    private static final String MOBILE_MONITOR_STOP = String.format("%s:%s:%s", MOBILE, MONITOR, STOP);
    private static final String MOBILE_NETWORK_SETTINGS_GET = String.format("%s:network.settings:get", MOBILE);
    private static final String MOBILE_NETWORK_VIRTUALIZATION_START = String.format("%s:%s:%s", MOBILE, VIRTUALIZATION, START);
    private static final String MOBILE_NETWORK_VIRTUALIZATION_STOP = String.format("%s:%s:%s", MOBILE, VIRTUALIZATION, STOP);
    private static final String MOBILE_OBJ_START = String.format("%s:%s:%s", MOBILE, OBJ_OPT, START);
    private static final String MOBILE_OBJ_STOP = String.format("%s:%s:%s", MOBILE, OBJ_OPT, STOP);
    private static final String MOBILE_PRESS_KEY = String.format("%s:presskey", MOBILE);
    private static final String MOBILE_RESET_LOCATION = String.format("%s:%s:reset", MOBILE, LOCATION);
    private static final String MOBILE_SET_LOCATION = String.format("%s:%s:set", MOBILE, LOCATION);
    private static final String MOBILE_TEXT_FIND = String.format("%s:%s:%s", MOBILE, TXT, FIND);
    private static final String MOBILE_TEXT_SET = String.format("%s:edit-%s:set", MOBILE, TXT);
    private static final String MOBILE_TOUCH_DRAG = String.format("%s:%s:drag", MOBILE, TOUCH);
    private static final String MOBILE_TOUCH_SWIPE = String.format("%s:%s:swipe", MOBILE, TOUCH);
    private static final String MOBILE_TOUCH_TAP = String.format("%s:%s:tap", MOBILE, TOUCH);

    @Step("Step >> Perfecto Commands: Install application with Perfecto API")
    public static void installApp() {
        String buildPass;
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_EXACT_VERSION))) {
            buildPass = ConfigProperties.getProperty(ProjectConfig.EXACT_VERSION);
        } else {
            buildPass = PerfectoAPI.getLatestBuildVersion(false);
        }
        Log.info("Perfecto Commands: installing application from Perfecto repository (%s)", buildPass);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.FILE, buildPass);
        params.put(PerfectoParameters.INSTRUMENT, PerfectoParameters.INSTRUMENT);
        if (AbstractDriverManager.isIos()) {
            params.put(PerfectoParameters.RESIGN, true);
        }
        executeScript(MOBILE_APP_INSTALL, params,
                      String.format("FAILED - application (%s) install", buildPass),
                      String.format("Installed the application: %s", buildPass));
    }

    @Step("Step >> Perfecto Commands: Launch application")
    public static void launchApp(String packageId) {
        Log.info("Perfecto Commands: launching application (%s)", packageId);
        PerfectoReport.stepStart("Starting the application with package name: " + packageId);
        Object result = executeScript(MOBILE_APP_OPEN, ImmutableMap.of(PerfectoParameters.IDENTIFIER, packageId),
                                      String.format("FAILED - application (%s) launch", packageId),
                                      String.format("Launched the application: %s", packageId));
        PerfectoReport.stepEnd();
        if (result instanceof Error) {
            Log.exception("FAILED - launch package ID: %s\n%s", packageId, result);
        }
    }

    @Step("Step >> Perfecto Commands: Launch activity")
    public static void launchActivity(String activity) {
        Log.info("Perfecto Commands: launching activity (%s)", activity);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.PACKAGE, "");
        params.put(PerfectoParameters.ACTIVITY, activity);
        Object result = executeScript(MOBILE_ACTIVITY_OPEN, params,
                                      String.format("FAILED - activity (%s) launch", activity),
                                      String.format("Launched the activity: %s", activity));
        if (result instanceof Error) {
            Log.exception("FAILED - launch Activity: %s\ns%", activity, result);
        }
    }

    @Step("Step >> Perfecto Commands: Close application")
    public static void closeApp(String packageId) {
        Log.info("Perfecto Commands: closing application (%s)", packageId);
        try {
            ((RemoteWebDriver) eventFiringWebDriver()).executeScript(MOBILE_APP_CLOSE, ImmutableMap.of
                    (PerfectoParameters.IDENTIFIER, packageId));
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(e.getMessage())
                    && e.getMessage().contains("Application isn't running")) {
                Log.info("%s - application is not running", packageId);
            }
        }
    }

    @Step("Step >> Perfecto Commands: Start activity")
    public static void startActivity(String packageId, String activity) {
        Log.info("Perfecto Commands: starting activity (%s)", packageId);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.PACKAGE, packageId);
        params.put(PerfectoParameters.ACTIVITY, activity);
        Object result = executeScript(MOBILE_ACTIVITY_OPEN, params,
                                      String.format("FAILED - unable to install application: %s", packageId),
                                      String.format("Started the application with activity: %s", packageId));
        if (result instanceof Error) {
            Log.exception("FAILED - start Activity ID: %s for package: %s\n%s",
                          params.get(PerfectoParameters.ACTIVITY),
                          params.get(PerfectoParameters.PACKAGE),
                          result);
        }
    }

    @Step("Step >> Perfecto Commands: Get activity parameter")
    public static String getActivityParam(String param) {
        Log.info("Perfecto Commands: getting activity parameter (%s)", param);
        Map<String, Object> params = new HashMap<>();
        params.put("property", param);
        return (String) executeScript(MOBILE_DEVICE_INFO, params,
                                      String.format("FAILED - unable to get param: %s", param),
                                      String.format("Got value of the application param: %s", param));
    }

    @Step("Step >> Perfecto Commands: Send text")
    public static void sendText(String label, String text) {
        Log.info("Perfecto Commands: sending text: %s, with label (%s)", text, label);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LABEL, label);
        params.put(PerfectoParameters.TEXT, text);
        params.put(PerfectoParameters.OPERATION, "single");
        params.put(PerfectoParameters.LABEL_DIRECTION, "below");
        params.put(PerfectoParameters.LABEL_OFFSET, "1%");
        executeScript(MOBILE_TEXT_SET, params, "", "");
    }

    @Step("Step >> Perfecto Commands: Send key")
    public static void sendKey(String key) {
        Log.info("Perfecto Commands: sending key (%s)", key);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.KEY, key); // key = 1 >> default?
        params.put(PerfectoParameters.METASTATE, "0");
        executeScript(MOBILE_KEY_EVENT, params, "", "");
    }

    @SneakyThrows
    @Step("Step >> Perfecto Commands: Uninstall application")
    public static void uninstallApp(String packageId) {
        Log.info("Perfecto Commands: uninstalling application (%s)", packageId);
        if (((AppiumDriver<?>) eventFiringWebDriver()).isAppInstalled(packageId)) {
            executeScript(MOBILE_APP_UNINSTALL, ImmutableMap.of(PerfectoParameters.IDENTIFIER, packageId),
                          String.format("FAILED - application (%s) uninstall", packageId),
                          String.format("Uninstalled the application: %s", packageId));
        }
    }

    @Step("Step >> Perfecto Commands: Check if application is installed")
    public static boolean isApplicationInstalled(String packageId) {
        Log.info("Perfecto Commands: checking application is installed (%s)", packageId);
        String appList = (String) ((RemoteWebDriver) eventFiringWebDriver())
                .executeScript(MOBILE_APP_FIND, ImmutableMap.of(PerfectoParameters.IDENTIFIER, packageId));
        return (appList.contains(packageId));
    }


    @Step("Step >> Perfecto Commands: Clean Application")
    public static boolean cleanApplication(String packageId) {
        Log.info("Perfecto Commands: clean application (%s)", packageId);
        String appList = (String) ((RemoteWebDriver) eventFiringWebDriver())
                .executeScript(MOBILE_APP_CLEAN, ImmutableMap.of(PerfectoParameters.IDENTIFIER, packageId));
        return (appList.contains(packageId));
    }

    @Step("Step >> Perfecto Commands: Execute tap")
    public static void tap(String coordinates) {
        Log.info("Perfecto Commands: tapping");
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LOCATION, coordinates);
        params.put(PerfectoParameters.DURATION, "1");
        executeScript(MOBILE_TOUCH_TAP, params, "FAILED - unable to tap", "Tapped");
    }

    @Step("Step >> Perfecto Commands: Execute tap center")
    public static boolean tapCenter() {
        Log.info("Perfecto Commands: tapping center");
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LOCATION, "30%,50%");
        params.put(PerfectoParameters.DURATION, "1");
        return executeWebDriverScript(
                MOBILE_TOUCH_TAP, params, "FAILED - unable to make center tap", "Performed center tap");
    }

    @Step("Step >> Perfecto Commands: Execute tap button by text")
    public static boolean tapButtonByText(String buttonText) {
        Log.info("Perfecto Commands: tapping button by text (%s)", buttonText);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LABEL, buttonText);
        params.put(PerfectoParameters.IGNORE_CASE, "case");
        return executeWebDriverScript(MOBILE_BTN_TXT_CLICK, params,
                                      String.format("FAILED - unable to tap on text: %s", buttonText),
                                      String.format("Performed tap by text: %s", buttonText));
    }

    @Step("Step >> Perfecto Commands: Execute tap button by text and index")
    public static boolean tapButtonByTextAndIndex(String buttonText, String index) {
        Log.info("Perfecto Commands: tapping button by text (%s) and index (%s)", buttonText, index);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LABEL, buttonText);
        params.put(PerfectoParameters.INDEX, index);
        params.put(PerfectoParameters.IGNORE_CASE, "case");
        return executeWebDriverScript(MOBILE_BTN_TXT_CLICK, params,
                                      String.format("FAILED - unable to click by text: %s", buttonText),
                                      String.format("Performed tap by text and index: %s | %s", buttonText, index));
    }

    public static boolean drag(int startX, int startY, int endX, int endY, int duration) {
        return drag(
                String.valueOf(startX), String.valueOf(startY),
                String.valueOf(endX), String.valueOf(endY),
                String.valueOf(duration));
    }

    @Step("Step >> Perfecto Commands: Execute drag")
    public static boolean drag(String startX, String startY, String endX, String endY, String duration) {
        Log.info("Perfecto Commands: dragging (startX=%s, startY=%s, endX=%s, endY=%s, duration=%s)",
                 startX, startY, endX, endY, duration);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LOCATION, TextUtils.format("{0},{1},{2},{3}", startX, startY, endX, endY));
        params.put(PerfectoParameters.DURATION, duration);
        return executeWebDriverScript(MOBILE_TOUCH_DRAG, params,
                                      "FAILED - unable to drag", "Dragged by location");
    }

    public static void dragByPercent(int startX, int startY, int endX, int endY, int duration) {
        dragByPercent(
                String.valueOf(startX), String.valueOf(startY),
                String.valueOf(endX), String.valueOf(endY),
                String.valueOf(duration));
    }

    @Step("Step >> Perfecto Commands: Execute drag by %")
    public static void dragByPercent(String startX, String startY, String endX, String endY, String duration) {
        Log.info("Perfecto Commands: dragging by percent (startX=%s, startY=%s, endX=%s, endY=%s, duration=%s)",
                 startX, startY, endX, endY, duration);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LOCATION, TextUtils.format("{0}%,{1}%,{2}%,{3}%", startX, startY, endX, endY));
        params.put(PerfectoParameters.DURATION, duration);
        executeScript(MOBILE_TOUCH_DRAG, params, "FAILED - unable to drag (by %)", "Dragged by percent");
    }

    @Step("Step >> Perfecto Commands: Swipe {0} {1} times")
    public static boolean swipe(Direction direction, int numberOfSwipes) {
        boolean isSuccess = false;
        for (int i = 1; i <= numberOfSwipes; i++) {
            isSuccess = swipe(direction);
        }
        return isSuccess;
    }

    public static boolean swipeUp() {
        return swipe(Direction.UP);
    }

    public static boolean swipeDown() {
        return swipe(Direction.DOWN);
    }

    public static boolean swipeRight() {
        return swipe(Direction.RIGHT);
    }

    public static boolean swipeLeft() {
        return swipe(Direction.LEFT);
    }

    public static boolean swipeUp(int times) {
        return swipe(Direction.UP, times);
    }

    public static boolean swipeDown(int times) {
        return swipe(Direction.DOWN, times);
    }

    public static boolean swipeRight(int times) {
        return swipe(Direction.RIGHT, times);
    }

    public static boolean swipeLeft(int times) {
        return swipe(Direction.LEFT, times);
    }

    @Step("Step >> Perfecto Commands: Press android system 'Home' button")
    public static void pressHomeButton() {
        Log.info("Perfecto Commands: pressing android system 'Home' button");
        executeScript(MOBILE_PRESS_KEY,
                      ImmutableMap.of(PerfectoParameters.KEY_SEQUENCE, AndroidKey.HOME.name()),
                      "FAILED - press 'Home' button", "");
    }

    @Step("Step >> Perfecto Commands: Search text on the screen")
    public static boolean searchText(String text) {
        Log.info("Perfecto Commands: searching text '%s' on the screen", text);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.CONTENT, text);
        params.put(PerfectoParameters.THRESHOLD, "80");
        String result = String.valueOf(((RemoteWebDriver) eventFiringWebDriver()).executeScript(MOBILE_TEXT_FIND, params));
        if (result.equals(Boolean.TRUE.toString())) {
            return Log.info("Found text as expected: %s", text);
        } else {
            return Log.warn("FAILED - find required text: %s", text);
        }
    }

    @Step("Step >> Perfecto Commands: Search text top screen: {0}")
    public static boolean searchTextTopScreen(String text) {
        Log.info("Perfecto Commands: searching on the top area of the screen for '%s'", text);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.CONTENT, text);
        params.put(PerfectoParameters.THRESHOLD, "80");
        params.put(PerfectoParameters.SCREEN_TOP, "0%");
        params.put(PerfectoParameters.SCREEN_LEFT, "0%");
        params.put(PerfectoParameters.SCREEN_WIDTH, "100%");
        params.put(PerfectoParameters.SCREEN_HEIGHT, "14%");
        String result = String.valueOf(((RemoteWebDriver) eventFiringWebDriver()).executeScript(MOBILE_TEXT_FIND, params));
        if (result.equals(Boolean.TRUE.toString())) {
            return Log.info("Found text as expected: %s", text);
        }
        return Log.warn("FAILED - find required text: %s", text);
    }

    @Step("Step >> Perfecto Commands: Search text")
    public static void searchText(String text, boolean throwException) {
        Log.info("Perfecto Commands: searching text '%s'", text);
        waitForText(text, 2);
        if (!searchText(text) && throwException) {
            throw new NotFoundException(String.format("The text NOT FOUND: %s", text));
        }
    }

    public static boolean waitForText(String expectedText) {
        return waitForText(expectedText, 5);
    }

    @SneakyThrows
    public static boolean waitForTextTopArea(String expectedText) {
        return waitForTextByArea(expectedText, 5);
    }

    @SneakyThrows
    @Step("Step >> Perfecto Commands: Wait for text")
    public static boolean waitForText(String expectedText, int retries) {
        Log.info("Perfecto Commands: waiting to appear for text '%s'", expectedText);
        int i = 0;
        boolean value = searchText(expectedText);
        while (!value && i < retries) {
            value = searchText(expectedText);
            i++;
        }
        return value;
    }

    @SneakyThrows
    @Step("Step >> Perfecto Commands: Wait for text by area")
    public static boolean waitForTextByArea(String expectedText, int retries) {
        Log.info("Perfecto Commands: waiting to appear for text '%s'", expectedText);
        int i = 0;
        boolean value = searchTextTopScreen(expectedText);
        while (!value && i < retries) {
            value = searchText(expectedText);
            i++;
        }
        return value;
    }

    public static void startTreeOptimization() {
        executeScript(MOBILE_OBJ_START, ImmutableMap.of(PerfectoParameters.CHILDREN, 2), "", "");
    }

    public static void stopTreeOptimization() {
        Map<String, Object> params = new HashMap<>();
        executeScript(MOBILE_OBJ_STOP, params, "", "");
    }

    public void searchImage(String imageLocator, boolean throwException) {
        if (!searchImage(imageLocator) && throwException) {
            throw new NotFoundException(String.format("The image NOT FOUND: %s", imageLocator));
        }
    }

    @Step("Step >> Perfecto Commands: Search for image on screen")
    public static boolean searchImage(String imageLocator) {
        Log.info("Perfecto Commands: searching for image on screen - '%s'", imageLocator);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.CONTENT, imageLocator);
        params.put(PerfectoParameters.THRESHOLD, "80");
        params.put(PerfectoParameters.MATCH, "Bounded");
        String result = String.valueOf(((RemoteWebDriver) eventFiringWebDriver()).executeScript(MOBILE_IMG_FIND, params));
        if (result.equals(Boolean.TRUE.toString())) {
            return Log.info("Found expected image");
        } else {
            return Log.warn("FAILED - find required image");
        }
    }

    public static void searchTextPressImage(String text, String directionToText, String image) {
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.CONTENT, text);
        params.put(PerfectoParameters.THRESHOLD, "80");
        executeScript(MOBILE_TEXT_FIND, params, "",
                      String.format("Searching for image: %s - next to the text: %s", image, text));
        params.clear();
        params.put(PerfectoParameters.LABEL, image);
        params.put(PerfectoParameters.THRESHOLD, "80");
        params.put(PerfectoParameters.MATCH, "Bounded");
        params.put(PerfectoParameters.OPERATION, "single");
        params.put(PerfectoParameters.RELATION_DIRECTION, directionToText);
        params.put(PerfectoParameters.RELATION_INLINE, "Horizontal");
        executeScript(MOBILE_BTN_IMG_CLICK, params, "", "");
    }

    public static void clickImage(String imageLocator) {
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.LABEL, imageLocator);
        params.put(PerfectoParameters.THRESHOLD, "80");
        params.put(PerfectoParameters.MATCH, "Bounded");
        params.put(PerfectoParameters.OPERATION, "single");
        executeScript(MOBILE_BTN_IMG_CLICK, params,
                      String.format("FAILED - unable to click on: %s", imageLocator),
                      String.format("Clicked image on screen: %s", imageLocator));
    }

    @Step("Step >> Perfecto Commands: Execute ADB shell command")
    public static Object executeADBShellCommand(String command) {
        return executeScript(MOBILE_HANDSET_SHELL, Collections.singletonMap(PerfectoParameters.COMMAND, command),
                             String.format("FAILED - unable to execute ADB shell command: %s", command),
                             String.format("Executed ADB shell command: %s", command));
    }

    public static void pressSystemButton(String button) {
        executeScript(MOBILE_PRESS_KEY, Collections.singletonMap(PerfectoParameters.KEY_SEQUENCE, button),
                      String.format("FAILED - unable to press system button: %s", button),
                      String.format("Pressed on button: %s", button));
    }

    @Step("Step >> Perfecto Commands: Grant android application permissions")
    public static void grantAndroidPermissions(String packageName) {
        Log.info("Perfecto Commands: granting Android permissions for package (%s)", packageName);
        Arrays.asList(Permission.RECORD_AUDIO, Permission.CAR_VENDOR_EXTENSION, Permission.ACCESS_FINE_LOCATION,
                      Permission.ACCESS_COARSE_LOCATION, Permission.READ_CALENDAR, Permission.WRITE_CALENDAR,
                      Permission.CAMERA, Permission.READ_CONTACTS, Permission.BLUETOOTH_CONNECT,
                      Permission.BLUETOOTH_SCAN, Permission.POST_NOTIFICATIONS).forEach
                (permission -> executeADBShellCommand(String.format("pm grant %s %s", packageName,
                                                                    permission.getCommand())));
    }

    public static void stopLimitTreeChildElementsAmount() {
        limitTreeChildElementsAmount(0, false);
    }

    @Step("Step >> Perfecto Commands: Limit tree child elements amount")
    public static void limitTreeChildElementsAmount(int amount, boolean isStart) {
        Log.info("Perfecto Commands: limiting tree child elements amount (%s)", amount);
        if (Objects.equals(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS), Boolean.TRUE.toString())) {
            if (isStart) {
                executeScript(MOBILE_OBJ_START,
                              Collections.singletonMap(PerfectoParameters.CHILDREN,
                                                       String.valueOf(amount)), "FAILED - unable to start tree optimization",
                              String.format("Tree optimization is started with the limit: %s", amount));
            } else {
                executeScript(MOBILE_OBJ_STOP, new HashMap<>(),
                              "FAILED - unable to stop tree optimization", "Tree optimization is stopped");
            }
        }
    }

    @Step("Step >> Perfecto Commands: Set device location by address")
    public static void setDeviceLocationByAddress(String address) {
        Log.info("Perfecto Commands: setting device location by address (%s)", address);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.ADDRESS, address);
        executeScript(MOBILE_SET_LOCATION, params,
                      "FAILED - unable to set the location to " + address,
                      "Perfecto device mocked location is set to " + address);
    }

    @Step("Step >> Perfecto Commands: Set device location by location (coordinates)")
    public static void setDeviceLocation(String location) {
        Log.info("Perfecto Commands: setting device location by location/coordinates (%s)", location);
        Map<String, Object> params = new HashMap<>();
        if (location.contains(", ") || location.contains(",")) {
            String[] locationArray = location.split(",");
            params.put(PerfectoParameters.COORDINATES, locationArray[0].trim() + "," + locationArray[1].trim());
        } else {
            String[] locationArray = location.split(" ");
            params.put(PerfectoParameters.COORDINATES, locationArray[0] + "," + locationArray[1]);
        }
        Log.info("Perfecto device mocked coordinates is set to %s", location);
        executeScript(MOBILE_SET_LOCATION, params,
                      "FAILED - unable to set the location to " + location,
                      "Perfecto device mocked location is set to " + location);
    }

    public static String getDeviceLocation() {
        String location = "";
        try {
            location = (String) executeScript(MOBILE_GET_LOCATION, new HashMap<>(),
                                              "FAILED - unable to get perfecto location",
                                              "Perfecto device location is");
            Log.info("Location: %s", location);
        } catch (Exception e) {
            Log.warn("FAILED - unable to get text value of the device location\n%s", e);
        }
        return location;
    }

    public static void resetLocation() {
        executeScript(MOBILE_RESET_LOCATION, new HashMap<>(), "FAILED - unable to reset the location",
                      "Perfecto mock location is reset");
    }

    @Step("Step >> Perfecto Commands: Get device log")
    public static void getDeviceLog(int lines, String testName) {
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.TAIL, lines);
        try {
            String result = (String) executeScript(MOBILE_DEVICE_LOG, params, "FAILED - unable to get device logs",
                                                   "Generating Perfecto device logs");
            storeLogDetails("Device", result);
            CustomSoftAssert.addAttachmentToAllure(
                    testName + "_device_log", DataUtils.writeToFile(result, "log").toPath());
        } catch (Exception | Error e) {
            Log.warn("FAILED - get Perfecto device logs\n%s", e);
        }
    }

    @Step("Step >> Perfecto Commands: Get ADB log")
    public static void getADBlog(int amountOfLines, String testName) {
        try {
            String pid = (String) executeADBShellCommand("pidof -s " + "app package name");
            String result = (String) executeADBShellCommand("logcat -d -t " + amountOfLines + " -v time p " + pid);
            storeLogDetails("ADB", result);
            CustomSoftAssert.addAttachmentToAllure(
                    testName + "_adb_log", DataUtils.writeToFile(result, "log").toPath());
        } catch (Exception | Error e) {
            Log.warn("FAILED - get ADB logs\n%s", e);
        }
    }

    @Step("Step >> Perfecto Commands: Get CPU data")
    public static void getCPUData() {
        String result = (String) executeADBShellCommand(
                "dumpsys cpuinfo | grep -m 1 " + "app package name" + " | grep -oE '[0-9]+%' | head -n 1");
        Log.info("CPU info: " + result);
    }

    @Step("Step >> Perfecto Commands: Get Memory data")
    public static void getMemoryData() {
        String pid = (String) executeADBShellCommand("pidof -s " + "app package name");
        String result = (String) executeADBShellCommand("dumpsys meminfo " + pid + " | grep -oP 'TOTAL PSS:\\s*\\K\\d+'");
        Log.info("Memory info (TOTAL PSS): " + result);
    }

    public static String[] getFilesList(String location) {
        String result = (String) executeADBShellCommand("ls " + location);
        String[] list = result.split("\n");
        Log.info("File list: " + result);
        return list;
    }

    public static void killAndStartAppByPackageId(String packageId) {
        closeApp(packageId);
        startActivity(packageId, "");
    }

    @Step("Step >> Perfecto Commands: Start vitals recording")
    public static void startVitalRecording(boolean logApp) {
        Log.info("Perfecto Commands: starting vital recording");
        Map<String, Object> params = new HashMap<>();
        String logType;
        if (logApp) {
            logType = "app name";
        } else {
            logType = "Device";
        }
        params.put(PerfectoParameters.SOURCES, logType);
        params.put(PerfectoParameters.INTERVAL, 10);
        params.put(PerfectoParameters.MONITORS, "all");
        try {
            executeScript(MOBILE_MONITOR_START, params,
                          String.format("FAILED - unable to start '%s' vitals monitoring", logType),
                          "Started vitals monitoring");
        } catch (Exception | Error e) {
            Log.warn("FAILED - start vitals recording\n%s", e);
        }
    }

    @Step("Step >> Perfecto Commands: Stop Vitals Recording")
    public static void stopVitalsRecording() {
        try {
            executeScript(MOBILE_MONITOR_STOP, new HashMap<>(), "FAILED - unable to stop vitals monitoring",
                          "Stopped vitals monitoring");
        } catch (Exception | Error e) {
            Log.warn("FAILED - stop vitals recording\n%s", e);
        }
    }

    public static boolean isNetworkEnabled(NetworkStatus network) {
        return getNetworkStatus(network).contains(network.getNetwork() + "=" + Boolean.TRUE);
    }

  @Getter
  @AllArgsConstructor
  public enum NetworkStatus {

    AIRPLANE_MODE   ("airplanemode"),
    DATA            ("data"),
    WIFI            ("wifi");

    private final String network;
  }

    @Step("Step >> Perfecto Commands: Get Network Status")
    public static String getNetworkStatus(NetworkStatus network) {
        Log.info("Perfecto Commands: getting network status (%s)", network);
        String status = ((String) executeScript(MOBILE_NETWORK_SETTINGS_GET, Collections.singletonMap
                                                        (PerfectoParameters.PROPERTY, network.getNetwork()),
                                                "FAILED - unable to get device network status",
                                                "Network status:"))
                .replace("{", "").replace("}", "");
        Log.info(status);
        return status;
    }

    public static void startLogcatLogging() {
        try {
            executeScript(MOBILE_LOGS_START, new HashMap<>(),
                          "FAILED - unable to start logcat logging",
                          "Logcat logging is started");
        } catch (Exception | Error e) {
            Log.warn("FAILED - vitals recording start\n%s", e);
        }
    }

    public static void stopLogcatLogging() {
        try {
            executeScript(MOBILE_LOGS_STOP, new HashMap<>(),
                          "FAILED - unable to stop logcat logging",
                          "Logcat logging is stopped");
        } catch (Exception | Error e) {
            Log.warn("FAILED - vitals recording stop\n%s", e);
        }
    }

    public static Object runBackDoorScript(String target, String methodName) {
        ImmutableMap<String, Object> scriptArgs = ImmutableMap.of(
                "target", target,
                "methods", Collections.singletonList(ImmutableMap.of("name", methodName)));
        return executeScript("mobile:backdoor", scriptArgs,
                             String.format("FAILED - unable to run the backdoor script with arguments: %s", scriptArgs.toString()),
                             "The backdoor script was executed");
    }

    public static void flashTheElement(By locator) {
        HashMap<String, Object> scriptArgs = new HashMap<>();
        MobileElement element = (MobileElement) eventFiringWebDriver().findElement(locator);
        scriptArgs.put("element", element.getId());
        scriptArgs.put("durationMillis", 50); // how long should each flash take?
        scriptArgs.put("repeatCount", 20);    // how many times should we flash?
        executeScript("mobile:flashElement", scriptArgs, "FAILED - unable to flash the element",
                      "The element was flashed");
    }

    public static void networkVirtualization(boolean isStarted) {
        if (Objects.equals(ConfigProperties.getProperty(ProjectConfig.IS_NETWORK_VIRTUALIZATION), Boolean.TRUE.toString())) {
            Map<String, Object> params = new HashMap<>();
            params.put(PerfectoParameters.HAR_FILE, Boolean.TRUE.toString());
            if (isStarted) {
                executeScript(MOBILE_NETWORK_VIRTUALIZATION_START, params, "FAILED - unable to start Perfecto network virtualization",
                        "Perfecto network virtualization is started");
            } else {
                params.clear();
                executeScript(MOBILE_NETWORK_VIRTUALIZATION_STOP, params, "FAILED - unable to stop Perfecto network virtualization",
                        "Perfecto network virtualization is stopped");
            }
        }
    }

    public static String getPerfectoToken() {
        Log.info("Perfecto Commands: getting Perfecto token");
        User user = User.getByID(getSysProperty(GlobalVariables.SYSTEM_USER_NAME.getValue()));
        Log.info("Current user '%s' with ID: '%s'", user, user.getId());
        switch (user) {
            case DORUM: {
                return ConfigProperties.getProperty(ProjectConfig.OATH_TOKEN_PERFECTO_DORUM);
            }
            default: {
                return ConfigProperties.getProperty(ProjectConfig.OATH_TOKEN_PERFECTO_DORUM);
            }
        }
    }

    public static boolean setPortraitDeviceOrientation() {
        return setDeviceOrientation("portrait");
    }

    public static boolean setLandscapeDeviceOrientation() {
        return setDeviceOrientation("landscape");
    }

    //--------------- Private Methods ---------------

    private static boolean swipe(String start, String end, int duration) {
        Log.info("Perfecto Commands: swiping (start=%s, end=%s, duration=%s)", start, end, duration);
        Map<String, Object> params = new HashMap<>();
        params.put(PerfectoParameters.START, start);
        params.put(PerfectoParameters.END, end);
        params.put(PerfectoParameters.DURATION, String.valueOf(duration));
        return executeWebDriverScript(MOBILE_TOUCH_SWIPE, params,
                                      "FAILED - unable to swipe from " + start + " to " + end,
                                      "Swiped from " + start + " to " + end);
    }

    @Step("Step >> Perfecto Commands: swipe {0}")
    private static boolean swipe(Direction direction) {
        boolean isSuccess = swipe(direction.getStart(), direction.getEnd(), 1);
        Log.info("Swiped %s", direction.getDirection());
        return isSuccess;
    }

    @Step("Step >> Perfecto Commands: Log details")
    private static void storeLogDetails(String type, String details) {
        Log.debug("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- %s Log (Start) -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-", type);
        Log.debug("Device Log:\n", details);
        Log.debug("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- %s Log (End) -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-", type);
    }

    @Step("Step >> Perfecto Commands: Set Device Orientation")
    private static boolean setDeviceOrientation(String orientation) {
        Map<String, Object> params = new HashMap<>();
        params.put("state", orientation);
        return executeWebDriverScript(MOBILE_DEVICE_ROTATE, params,
                "FAILED - unable to set device orientation",
                "Set device orientation");
    }

    private static boolean executeWebDriverScript(
            String command, Map<String, Object> params, String error, String comment) {
        Object result = executeScript(command, params, error, comment);
        if (result instanceof Error) {
            return Log.warn(((Error) result).getMessage());
        }
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return true;
    }

    @SneakyThrows
    private static Object executeScript(String command, Map<String, Object> params, String error, String comment) {
        Log.info("Perfecto Commands > executing script: %s\nParameters: %s", command, params);
        int attempt = 0;
        Object response = new Object();
        while (attempt < 2) {
            try {
                response = ((RemoteWebDriver) eventFiringWebDriver()).executeScript(command, params);
                if (!comment.isEmpty()) {
                    Log.info(comment);
                }
                break;
            } catch (Exception e) {
                attempt++;
                if (attempt == 2) {
                    if (!error.isEmpty()) {
                        Log.warn("%s\n%s", error, e);
                    }
                    return new Error(Log.makeMessage(error, command, e));
                }
            }
        }
        return response;
    }

    private static WebDriver eventFiringWebDriver() {
        return ((EventFiringWebDriver) WebDriverContainer.getDriver()).getWrappedDriver();
    }

  public static String getSysProperty(String property) {
    try {
      return System.getProperty(property);
    } catch (Exception e) {
      Log.warn("FAILED - unable to find system property: %s\n%s", property, e);
    }
    return "";
  }
}
