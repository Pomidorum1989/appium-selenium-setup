package org.dorum.automation;

import com.github.automatedowl.tools.AllureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;
import com.perfecto.reportium.client.ReportiumClient;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.driver.DriverFactory;
import org.dorum.automation.common.utils.*;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.perfecto.*;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.dorum.automation.common.utils.enums.AllureReportParameter.*;

@Log4j2
public class TestBase {

    protected CustomSoftAssert softAssert;
    // Services

    // Pages
    
    
    // Local usage
    private static String testSuiteName;
    private static ImmutableMap.Builder<String, String> allureEnvVars;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) {
      DriverFactory.getDriver();
      DriverFactory.unRegisterEventListener();
      initServices();
      initPages();
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest(ITestContext context) {;
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext context) {
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext context, Method method) {
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
    }

    @AfterClass(alwaysRun = true)
    public void afterClass(ITestContext context) {
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite(ITestContext context) {;
    }


    @Step("Step >> Get Allure Environment Variables")
    protected void initBasicVarForAllureReport(ReportiumClient client) {
        log.info("Getting Allure environment variables");
        String deviceID, platformVersion, pdfReport, executionId;
        if (AbstractDriverManager.isAndroid()) {
            deviceID = AppiumCommands.getDesiredCapabilityValue(CapabilityName.DEVICE_NAME);
            platformVersion = AppiumCommands.getDesiredCapabilityValue(CapabilityName.PLATFORM_VERSION);
            pdfReport = AppiumCommands.getDesiredCapabilityValue(CapabilityName.REPORT_PDF_URL);
            executionId = AppiumCommands.getDesiredCapabilityValue(CapabilityName.EXECUTION_ID);
        } else {
            deviceID = (String) AppiumCommands.getCapability(CapabilityName.DEVICE_NAME);
            platformVersion = (String) AppiumCommands.getCapability(CapabilityName.PLATFORM_VERSION);
            pdfReport = (String) AppiumCommands.getCapability(CapabilityName.REPORT_PDF_URL);
            executionId = (String) AppiumCommands.getCapability(CapabilityName.EXECUTION_ID);
        }
        allureEnvVars = ImmutableMap.<String, String>builder()
                .put(APPLICATION_VERSION.getValue(), PerfectoAPI.artifactName)
                .put(PERFECTO_SESSION_ID.getValue(), Objects.requireNonNull(AppiumCommands.getSessionId()))
                .put(EXECUTION_ID.getValue(), executionId)
                .put(PERFECTO_REPORT_LINK.getValue(), client.getReportUrl())
                .put(PERFECTO_PDF_REPORT_LINK.getValue(), pdfReport)
                .put(DEVICE_PLATFORM.getValue(), ConfigProperties.getProperty(ProjectConfig.DRIVER_NAME))
                .put(DEVICE_MODEL.getValue(), PerfectoAPI.getDeviceParameter(deviceID, PerfectoDeviceParam.MODEL))
                .put(PLATFORM_VERSION.getValue(), platformVersion)
                .put(DEVICE_ID.getValue(), deviceID)
                .put(DEVICE_LANGUAGE.getValue(), PerfectoAPI.getDeviceParameter(deviceID, PerfectoDeviceParam.LANGUAGE))
                .put(DEVICE_LOCATION.getValue(), PerfectoAPI.getDeviceParameter(deviceID, PerfectoDeviceParam.LOCATION))
                .put(NETWORK_STATUS.getValue(), PerfectoCommands.getNetworkStatus(PerfectoCommands.NetworkStatus.DATA));
    }

    @SafeVarargs
    @Step("Step >> Generate Device Information")
    public final void addAllureValueToReport(boolean save, ImmutablePair<String, String>... pair) {
        log.info("Generating device information");
        try {
            for (ImmutablePair<String, String> immutablePair : pair) {
                allureEnvVars.put(immutablePair.getKey(), immutablePair.getValue());
            }
            if (save) {
                log.info("----------------------- Allure environment variables start ------------------------------");
                allureEnvVars.build().forEach((key, value) -> log.info("{} : {}", key, value));
                AllureEnvironmentWriter.allureEnvironmentWriter(allureEnvVars.build());
                log.info("----------------------- Allure environment variables end ------------------------------");
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }


    //--------------- Private Methods ---------------

    private void initServices() {
    }

    private void initPages() {
        log.info("Common initializations of Page Objects (Pages)");
    }


    private String getTestSuiteName() {
        return Objects.requireNonNullElseGet(testSuiteName, () -> testSuiteName = "Default Suite");
    }


    @SneakyThrows
    @Step("Step >> Create screenshot")
    private void screenShotOnFail(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String dateTime = DateUtils.getFormattedDateAsString(DateUtils.PATTERN_DMY_HM);
        File screenShot = AppiumCommands.takeScreenshot(
                TextUtils.format("target{0}screenshots", File.separator),
                TextUtils.format("{0}_{1}_{2}",
                                 testName, ConfigProperties.getProperty(ProjectConfig.ANDROID_DEVICE_ID), dateTime));
        CustomSoftAssert.addAttachmentToAllure(testName + "_" + dateTime, screenShot.toPath());
    }
}
