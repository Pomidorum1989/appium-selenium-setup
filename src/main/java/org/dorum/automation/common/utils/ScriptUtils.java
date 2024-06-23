package org.dorum.automation.common.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.driver.WebDriverContainer;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.dorum.automation.common.utils.enums.TitleName;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ScriptUtils {

    private static final JavascriptExecutor driver = (JavascriptExecutor) WebDriverContainer.getDriver();
    private static final String ERROR = "FAILED - unable to execute the script: {}\n{}";
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
            log.error("INVALID script - NULL/EMPTY");
        }
        if ((objects == null) || (objects.length == 0)) {
            log.error("INVALID list of objects - NULL/EMPTY");
        }
        AppiumCommands.switchContextByTitle(TitleName.APP_TITLE);
        log.info("Executing script (JavascriptExecutor): {}", script);
        driver.executeScript(script, objects);
    }

    public static boolean executeJavaScript(String scriptToExecute, String comment, boolean isFailOnError) {
        Object result = executeGenericScript(scriptToExecute, comment, "", isFailOnError);
        if (result instanceof Error) {
            log.warn(((Error) result).getMessage());
            return false;
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
                log.warn(((Error) result).getMessage());
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
        log.info("Executing async javascript: {}", scriptToExecute);
        if (!AppiumCommands.switchContextUsingTitle(TitleName.APP_TITLE)) {
            return new Error("Switch context failed");
        }
        try {
            AppiumCommands.setScriptTimeout(60);
            Object result = driver.executeAsyncScript(scriptToExecute);
            if (!comment.isEmpty()) {
                log.info(comment);
            }
            return result;
        } catch (Exception e) {
            if (isFailOnError) {
                log.error(ERROR, scriptToExecute, e);
            } else {
                log.warn(ERROR, scriptToExecute, e);
            }
            return new Error(e);
        }
    }

    public static <T> T executeGenericScript(
            String scriptToExecute, String comment, String failure, boolean isFailOnError,
            TitleName titleName, Object... var2) {
        log.info("Executing generic javascript: {}", scriptToExecute);
        if (!AppiumCommands.switchContextUsingTitle(titleName)) {
            return (T) new Error(String.format("FAILED - switch context by title %s", titleName));
        }
        if (StringUtils.isNotEmpty(comment)) {
            log.info(comment);
        }
        T result;
        try {

            result = (T) driver.executeScript(scriptToExecute, var2);

            if (result == null) {
                log.info("Execution result is NULL, but - call pass (returns TRUE)");
                return (T) Boolean.TRUE;
            }
        } catch (Exception e) {

            if (StringUtils.isNotEmpty(failure)) {
                failure = (failure.contains("FAILED - "))
                        ? failure + "\n%s"
                        : String.format("FAILED - %s", failure) + "\n%s";
            } else {
                failure = ERROR;
            }

            if (isFailOnError) {
                log.error(failure, scriptToExecute, e);
            } else {
                log.warn(failure, scriptToExecute, e);
            }

            return (T) new Error(e);
        }

        return result;
    }
}
