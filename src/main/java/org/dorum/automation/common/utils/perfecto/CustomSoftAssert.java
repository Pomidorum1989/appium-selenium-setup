package org.dorum.automation.common.utils.perfecto;

import com.epam.reportportal.listeners.LogLevel;
import com.epam.reportportal.service.ReportPortal;
import com.perfecto.reportium.client.ReportiumClient;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.consts.CapabilityName;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.utils.DateUtils;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.TextUtils;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;

@NoArgsConstructor
public class CustomSoftAssert extends SoftAssert {

    private static ReportiumClient client = PerfectoReport.REPORTIUM_CLIENT;

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        String screenshotName = assertCommand.getMessage();
        if (StringUtils.isEmpty(screenshotName)) {
            screenshotName = "No error message provided";
        }
        storeScreenshot(screenshotName);
        AppiumCommands.recordPerformanceData();
        try {
            if (client == null) {
                client = PerfectoReport.getReportClientInstance();
            }
            client.reportiumAssert(ex.getMessage(), false);
        } catch (Exception e) {
            Log.warn("Perfecto report isn't initialized\n%s", e);
        }
    }

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        try {
            if (client == null) {
                client = PerfectoReport.getReportClientInstance();
            }
            client.reportiumAssert(assertCommand.getMessage(), true);
        } catch (Exception e) {
            Log.warn("Perfecto report isn't initialized\n%s", e);
        }
    }

    public static boolean storeScreenshotOnFail(String screenshotName) {
        storeScreenshot(screenshotName);
        return false;
    }

    @Step("Step >> Store Screenshot {0}")
    public static File storeScreenshot(String screenshotName) {
        String dateTime = DateUtils.getFormattedDateAsString(DateUtils.PATTERN_DMY_HM);
        if (StringUtils.isEmpty(screenshotName)) {
            screenshotName = "CurrentState";
        }
        String deviceId;
        if (AbstractDriverManager.isAndroid()) {
            deviceId = AppiumCommands.getDesiredCapabilityValue(CapabilityName.DEVICE_NAME);
        } else {
            deviceId = (String) AppiumCommands.getCapability(CapabilityName.DEVICE_NAME);
        }
        File screenShot = AppiumCommands.takeScreenshot(
                TextUtils.format("target{0}screenshots", File.separator),
                TextUtils.format("{0}_{1}_{2}", screenshotName, deviceId, dateTime));
        addAttachmentToAllure(screenShot.getName().replace(":", ""), screenShot.toPath());
        return screenShot;
    }

    public static void addAttachmentToAllure(String attachmentName, Path attachment) {
        try {
            Allure.addAttachment(attachmentName, Files.newInputStream(attachment));
            addAttachmentToReportPortal(attachmentName, attachment);
            Log.info("Attachment is added to allure report with name: %s", attachmentName);
        } catch (Exception e) {
            Log.warn("FAILED - unable to attach attachment %s to RP/Allure report\n%s", attachmentName, e);
        }
    }

    public static void addAttachmentToReportPortal(String attachmentName, Path attachment) {
        ReportPortal.emitLog(attachmentName, LogLevel.INFO.name(), Calendar.getInstance().getTime(), attachment.toFile());
        Log.info("Attachment is added to Report Portal with name: %s", attachmentName);
    }
}
