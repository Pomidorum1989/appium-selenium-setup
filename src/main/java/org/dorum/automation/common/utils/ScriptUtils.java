package org.dorum.automation.common.utils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.driver.WebDriverContainer;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.dorum.automation.common.utils.enums.TitleName;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ScriptUtils {

    private static final JavascriptExecutor driver = (JavascriptExecutor) WebDriverContainer.getDriver();
    private static final String ERROR = "FAILED - unable to execute the script: %s\n%s";
    private static final String ARGUMENTS = "var callback = arguments[arguments.length - 1]; ";
    private static final String PROMISE = ".then(() => callback(true)).catch(() => callback(false));";

    public static Object executeScript(String comment, String script) {
        return executeGenericScript(script, comment, "", false);
    }

    public static Object executeScript(String comment, String script, TitleName titleName) {
        return executeGenericScript(script, comment, "", false, titleName);
    }

    public static void scrollToElementByScript(WebElement webElement) {
        executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public static void centerOnElementByScript(WebElement webElement) {
        executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", webElement);
    }

    private static void executeScript(String script, Object... objects) {
        if (StringUtils.isEmpty(script)) {
            Log.exception("INVALID script - NULL/EMPTY");
        }
        if ((objects == null) || (objects.length == 0)) {
            Log.exception("INVALID list of objects - NULL/EMPTY");
        }
        AppiumCommands.switchContextByTitle(TitleName.APP_TITLE);
        Log.info("Executing script (JavascriptExecutor): %s", script);
        driver.executeScript(script, objects);
    }

    public static boolean executeJavaScript(String scriptToExecute, String comment, boolean isFailOnError) {
        Object result = executeGenericScript(scriptToExecute, comment, "", isFailOnError);
        if (result instanceof Error) {
            return Log.warn(((Error) result).getMessage());
        }
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return true;
    }

    public static <T> T executeGenericScript(
            String scriptToExecute, String comment, String failure, boolean isFailOnError, Object... var2) {
        return executeGenericScript(scriptToExecute, comment, failure, isFailOnError, TitleName.APP_TITLE, var2);
    }

    @SneakyThrows
    public static boolean executeScript(String command, String comment, boolean isAsync, boolean isFailOnError) {
        boolean isSuccess = true;
        if (isAsync) {
            command = TextUtils.format("{0}{1}{2}", ARGUMENTS, command, PROMISE);
            Object result = executeAsyncJavaScript(command, comment, isFailOnError);
            if (result instanceof Error) {
                Log.warn(((Error) result).getMessage());
                isSuccess = false;
            }
            if (result instanceof Boolean) {
                isSuccess = (Boolean) result;
            }
        } else {
            isSuccess = executeJavaScript(command, comment, isFailOnError);
        }
        if (Optional.of(isSuccess).orElse(false)) {
          TimeUnit.SECONDS.sleep(10);
        }
        return isSuccess;
    }

    public static Object executeAsyncJavaScript(String scriptToExecute, String comment, boolean isFailOnError) {
        Log.info("Executing async javascript: %s", scriptToExecute);
        if (!AppiumCommands.switchContextUsingTitle(TitleName.APP_TITLE)) {
            return new Error(Log.makeMessage(ERROR, scriptToExecute, "Switch context failed"));
        }
        try {
            AppiumCommands.setScriptTimeout(60);
            Object result = driver.executeAsyncScript(scriptToExecute);
            if (!comment.isEmpty()) {
                Log.info(comment);
            }
            return result;
        } catch (Exception e) {
            if (isFailOnError) {
                Log.exception(ERROR, scriptToExecute, e);
            } else {
                Log.warn(ERROR, scriptToExecute, e);
            }
            return new Error(Log.makeMessage(ERROR, scriptToExecute, e));
        }
    }

    public static <T> T executeGenericScript(
            String scriptToExecute, String comment, String failure, boolean isFailOnError,
            TitleName titleName, Object... var2) {
        Log.info("Executing generic javascript: %s", scriptToExecute);
        if (!AppiumCommands.switchContextUsingTitle(titleName)) {
            return (T) new Error(String.format("FAILED - switch context by title %s", titleName));
        }
        if (StringUtils.isNotEmpty(comment)) {
            Log.info(comment);
        }
        T result;
        try {
            result = (T) driver.executeScript(scriptToExecute, var2);
            if (result == null) {
                Log.info("Execution result is NULL, but - call pass (returns TRUE)");
                return (T) Boolean.TRUE;
            }
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(failure)) {
                if (failure.contains("FAILED - ")) {
                    failure = failure + "\n%s";
                } else {
                    failure = String.format("FAILED - %s", failure) + "\n%s";
                }
            } else {
                failure = ERROR;
            }
            if (isFailOnError) {
                Log.exception(failure, scriptToExecute, e);
            } else {
                Log.warn(failure, scriptToExecute, e);
            }
            return (T) new Error(Log.makeMessage(failure, scriptToExecute, e));
        }
        return result;
    }
}
