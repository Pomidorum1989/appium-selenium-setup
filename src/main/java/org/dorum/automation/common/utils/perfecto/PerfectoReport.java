package org.dorum.automation.common.utils.perfecto;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dorum.automation.common.driver.WebDriverContainer;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.DateUtils;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.dorum.automation.common.utils.enums.GlobalVariables.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PerfectoReport {

    public static final ReportiumClient REPORTIUM_CLIENT;
    public static final String PROJECT_NAME = "";
    public static final String CUSTOM_FIELD = "Executed by";
    public static final String CUSTOM_FIELD_1 = "Automation Framework";
    public static final String CONTEXT_TAG = "";
    public static final String USER_NAME = "";
    public static final String GIT_PREFIX = "github:";

    static {
        REPORTIUM_CLIENT = getReportClientInstance();
    }

    public static ReportiumClient getReportClientInstance() {
        ReportiumClient client = null;
        String branchName = "local_branch";
        String gitURL = GIT_PREFIX + "";
        String commit = "local_commit";
        int buildNumber = DateUtils.getDateAsNumber();
        String jobName = "local_run";
        String platform = "local_platform";
        try {
            if (getEnvsVariable(GIT_BRANCH.getValue()) != null) {
                branchName = getEnvsVariable(GIT_BRANCH.getValue());
                gitURL = GIT_PREFIX + getEnvsVariable(GIT_URL.getValue());
                commit = getEnvsVariable(GIT_COMMIT.getValue());
                buildNumber = Integer.parseInt(getEnvsVariable(BUILD_NUMBER.getValue()));
                jobName = getEnvsVariable(JOB_NAME.getValue());
                platform = "Jenkins";
            } else if (getEnvsVariable(BUILD_SOURCE_BRANCH.getValue()) != null) {
                branchName = getEnvsVariable(BUILD_SOURCE_BRANCH.getValue());
                gitURL = GIT_PREFIX + getEnvsVariable(BUILD_REPOSITORY_URI.getValue());
                commit = getEnvsVariable(BUILD_SOURCE_VERSION.getValue());
                buildNumber = Integer.parseInt(getEnvsVariable(RELEASE_RELEASE_ID.getValue()));
                jobName = getEnvsVariable(BUILD_DEFINITION_NAME.getValue());
                platform = "Azure";
            }
            Log.info("Parameters for '%s' Perfecto reporting:", platform);
            Log.info("Branch name: %s", branchName);
            Log.info("Git url: %s", gitURL);
            Log.info("Commit: %s", commit);
            Log.info("Build number: %s", buildNumber);
            Log.info("Job name: %s", jobName);
            if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.PERFECTO_STATUS))) {
                client = new ReportiumClientFactory().createPerfectoReportiumClient(
                        new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                                .withProject(new Project(PROJECT_NAME, "Version: "
                                        + ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION)))
                                .withJob(new Job(jobName, buildNumber).withBranch(branchName)).withCustomFields(
                                        new CustomField(CUSTOM_FIELD, USER_NAME),
                                        new CustomField(PERFECTO_REPO_URL.getValue(), gitURL),
                                        new CustomField(PERFECTO_COMMIT.getValue(), commit),
                                        new CustomField(CUSTOM_FIELD_1, "Appium_TestNG_Java11"))
                                .withContextTags(CONTEXT_TAG)
                                .withWebDriver(WebDriverContainer.getDriver())
                                .build());
            } else {
                client = createLocalReportiumClient();
            }
        } catch (Exception e) {
            Log.warn("FAILED - get Perfecto Report client instance");
        }
        return client;
    }

    private static ReportiumClient createLocalReportiumClient() {
        Log.info("Starting Perfecto local reporting instance");
        return new ReportiumClientFactory().createLoggerClient();
    }

    public static void perfectoTestStart(ITestContext context, Method method) {
        final String[] className = new String[1];
        context.getCurrentXmlTest().getClasses().stream().findFirst().ifPresent(xmlClass -> className[0]
                = ("src.test.java." + xmlClass.getName()).replace(".", File.separator) + ".java");
        try {
            REPORTIUM_CLIENT.testStart(method.getAnnotation(Test.class).description(), new TestContext.Builder<>()
                    .withTestExecutionTags("Suite: '" + context.getSuite().getName() + "'")
                    .withTestExecutionTags("App version: '" + ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION) + "'")
                    .withCustomFields(new CustomField(PERFECTO_FILE_PATH.getValue(), className[0]))
                    .build());
            Log.info("Test source location for Perfecto report: " + className[0]);
        } catch (Exception e) {
            Log.warn("FAILED - start Perfecto report for test: %s\n%s", method.getName(), e);
        }
    }

    public static void perfectoTestStop(ITestResult result, PerfectoFailureReasons reasons) {
        try {
            if (result.isSuccess()) {
                if (!(Objects.equals(AppiumCommands.getSessionId(), null))) {
                    REPORTIUM_CLIENT.testStop(TestResultFactory.createSuccess());
                    Log.info("Test method '%s' was saved with status to the Perfecto report", result.getName());
                }
            } else {
                TestContext testContextEnd = new TestContext.Builder<>().withTestExecutionTags("Test failed").build();
                REPORTIUM_CLIENT.testStop(TestResultFactory.createFailure(
                        "Test: " + result.getName() + " is failed\n", result.getThrowable(),
                        reasons.getFailure()), testContextEnd);
                Log.warn("Test " + result.getName() + " was saved with following reason " + reasons +
                                  " to the Perfecto report");
            }
        } catch (Exception e) {
            Log.warn("FAILED - Perfecto stop test\n%s", e);
        }
    }

    public static String getReportUrl() {
        String link = REPORTIUM_CLIENT.getReportUrl();
        Log.info("Perfecto report link: '%s'", link);
        return link;
    }

    public static void stepStart(String comment) {
        Log.debug(comment);
        try {
            REPORTIUM_CLIENT.stepStart(comment);
        } catch (Exception e) {
            Log.warn("FAILED - Perfecto Report step START\n%s", e);
        }
    }

    public static void stepEnd() {
        try {
            REPORTIUM_CLIENT.stepEnd();
        } catch (Exception e) {
            Log.warn("FAILED - Perfecto Report step END\n%s", e);
        }
    }

    public static void addComment(String comment) {
        REPORTIUM_CLIENT.stepStart(comment);
        REPORTIUM_CLIENT.stepEnd();
    }

  public static String getEnvsVariable(String variableName) {
    String variable;
    try {
      variable = System.getenv(variableName);
    } catch (Exception e) {
      variable = "";
      Log.warn("FAILED - unable to get system variable: '%s'\n%s", variableName, e);
    }
    return variable;
  }
}
