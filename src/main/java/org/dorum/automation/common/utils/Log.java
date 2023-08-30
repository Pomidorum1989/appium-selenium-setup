package org.dorum.automation.common.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.restassured.config.LogConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.LoggerContext;
import org.openqa.selenium.By;
import org.testng.Reporter;
import org.testng.SkipException;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Log {

    static {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(new File("log4j2.xml").toURI());
    }

    private static final String ANSI = "\u001B[%sm";
    public static final String ANSI_RESET = String.format(ANSI, 0);
    public static final String ANSI_RED = String.format(ANSI, 31);
    public static final String ANSI_GREEN = String.format(ANSI, 32);
    public static final String ANSI_YELLOW = String.format(ANSI, 33);
    public static final String ANSI_BLUE = String.format(ANSI, 34);
    public static final String ANSI_PURPLE = String.format(ANSI, 35);
    public static final String ANSI_CYAN = String.format(ANSI, 36);
    public static final String ANSI_WHITE = String.format(ANSI, 37);
    private static final Logger LOGGER = LogManager.getLogger(Log.class);

    private static final Marker FOLDABLE_MARKER = MarkerManager.getMarker("FOLDABLE_MARKER");
    public static final LogConfig LOG_CONFIG = new LogConfig().defaultStream(new Log4jPrintStream(LOGGER));

    public static void simpleInfo(String message, Object... params) {
        LOGGER.info(message, params);
        Reporter.log(message);
    }

    public static boolean info(String message, Object... params) {
        reportByAllure(message, Level.INFO, params);
        return true;
    }

    public static boolean warn(String message, Object... params) {
        reportByAllure(message, Level.WARN, params);
        return false;
    }

    public static void error(String message, Object... params) {
        reportByAllure(message, Level.ERROR, params);
    }

    public static void fatal(String message, Object... params) {
        reportByAllure(message, Level.FATAL, params);
    }

    public static void debug(String message, Object... params) {
        reportByAllure(message, Level.DEBUG, params);
    }

    public static boolean exception(String message, Object... params) {
        exceptionHandling(message, false, params);
        return false;
    }

    public static void skipException(String message, Object... params) {
        exceptionHandling(message, true, params);
    }

    public static String getConcealedTextByDemand(String text, boolean isHideText) {
        if (isHideText) {
            return "XXXX";
        }
        return text;
    }

    //--------------- Private Methods ---------------

    private static void exceptionHandling(String message, boolean isSkip, Object... params) {
        message = reportByAllure(message, Level.FATAL, params);
        if (isSkip) {
            throw new SkipException(message);
        } else {
            throw new RuntimeException(message);
        }
    }

    private static String reportByAllure(String message, Level logLevel, Object... params) {
        message = makeMessage(message, params);
        String uuid = UUID.randomUUID().toString();
        Status status = Status.PASSED;
        String id = Level.INFO.name();
        String ansi = "", ansiReset = "";
        switch (logLevel.getStandardLevel()) {
            case DEBUG:
                id = Level.DEBUG.name();
                ansiReset = ANSI_RESET;
                ansi = ANSI_BLUE;
                break;
            case WARN:
                id = Level.WARN.name();
                ansiReset = ANSI_RESET;
                ansi = ANSI_YELLOW;
                status = Status.BROKEN;
                break;
            case ERROR:
                id = Level.ERROR.name();
                ansiReset = ANSI_RESET;
                ansi = ANSI_RED;
                status = Status.FAILED;
                break;
            case FATAL:
                id = Level.FATAL.name();
                ansiReset = ANSI_RESET;
                ansi = ANSI_RED;
                status = Status.FAILED;
                break;
            case TRACE:
                id = Level.TRACE.name();
                ansiReset = ANSI_PURPLE;
                ansi = ANSI_PURPLE;
                break;
        }
        Allure.getLifecycle().startStep(uuid, new StepResult().setName(message).setStatus(status));
        message = String.format("%s: %s", id, message);
        switch (logLevel.getStandardLevel()) {
            case DEBUG:
                LOGGER.debug(message);
                break;
            case WARN:
                LOGGER.warn(message);
                break;
            case ERROR:
                LOGGER.error(message);
                break;
            case FATAL:
                LOGGER.fatal(message);
                break;
            case TRACE:
                LOGGER.trace(FOLDABLE_MARKER, message);
            default: // INFO
                LOGGER.info(message);
                break;
        }
        Allure.getLifecycle().stopStep(uuid);
        Reporter.log(reviewValue("%s%s: %s%s", ansi, id, message, ansiReset), false);
        return message;
    }

    public static String makeMessage(String message, Object... params) {
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i++) {
                if ((params[i] instanceof Exception) || (params[i] instanceof Error)) {
                    String error;
                    if (params[i] instanceof Exception) {
                        error = ((Exception) params[i]).getMessage();
                    } else {
                        error = ((Error) params[i]).getMessage();
                    }
                    if (StringUtils.isNotEmpty(error)) {
                        if (error.length() > 300) {
                            if (error.contains(". ")) {
                                error = error.substring(0, error.indexOf(". ") + 1); // First semantic sentence
                            } else if (error.contains(".\n")) {
                                error = error.substring(0, error.indexOf("\n")); // First semantic sentence
                            }
                        }
                        if (error.length() > 300) {
                            boolean isPatternFound = false;
                            String pattern1 = "For documentation on this error"; // Selenium based exception
                            String pattern2 = "(Session info: chrome"; // Selenium based exception
                            String pattern3 = "Build info: version"; // Selenium based exception
                            if (error.contains(pattern1)) {
                                error = error.substring(0, error.indexOf(pattern1));
                                isPatternFound = true;
                            }
                            if (error.contains(pattern2)) {
                                error = error.substring(0, error.indexOf(pattern2));
                                isPatternFound = true;
                            }
                            if (error.contains(pattern3)) {
                                error = error.substring(0, error.indexOf(pattern3));
                                isPatternFound = true;
                            }
                            if (!isPatternFound) {
                                error = reviewValue("%s <<=[EXCEPTION, 1st 270 chars]", error.substring(0, 270));
                            }
                        }
                    } else {
                        error = getStackTrace(params[i]);
                    }
                    params[i] = error;
                }
            }
            if (!message.contains("%s") && !message.contains("%d") && !message.contains("%c")) {
                message = message + "\n%s";
            }
            return reviewValue(message, params);
        }
        return message;
    }

    private static String getStackTrace(Object error) {
        StackTraceElement[] stackTrace;
        if (error instanceof Exception) {
            stackTrace = ((Exception) error).getStackTrace();
        } else if (error instanceof Error) {
            stackTrace = ((Error) error).getStackTrace();
        } else {
            return null;
        }
        if ((stackTrace != null) && (stackTrace.length > 0)) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; (i < stackTrace.length) && (i < 5); i++) { // First 5 lines
                result.append(stackTrace[i].toString());
            }
            return result.toString();
        }
        return null;
    }

    private static String reviewValue(String message, Object... values) {
        String percent = "%";
        String placeholder = "%s";
        String percentTmp = "Tm#P0";
        String placeholderTmp = "Tm#P1";
        for (int i = 0; i < values.length; i++) {
            if ((values[i] instanceof String) && ((String) values[i]).contains(placeholder)) {
                values[i] = ((String) values[i]).replace(placeholder, placeholderTmp);
            } else if ((values[i] instanceof By) && values[i].toString().contains(placeholder)) {
                values[i] = values[i].toString().replace(placeholder, placeholderTmp);
            }
        }
        if (message.contains("%")) {
            message = message.replaceAll("%(?![sS])(.)", percentTmp);
        }
        if (StringUtils.countMatches(message, "%s") == values.length) {
            message = String.format(message, values);
        } else {
            AtomicReference<String> allValues = new AtomicReference<>("Values: ");
            Arrays.stream(values).forEach(item -> allValues.set(allValues.get() + item + ", "));
            message = message + "; " + allValues;
        }
        return message.replace(placeholderTmp, placeholder).replace(percentTmp, percent);
    }

    private static class Log4jPrintStream extends PrintStream {
        private final Logger logger;

        public Log4jPrintStream(Logger logger) {
            super(System.out);
            this.logger = logger;
        }

        @Override
        public void println(String message) {
            logger.info(FOLDABLE_MARKER, message);
        }
    }
}
