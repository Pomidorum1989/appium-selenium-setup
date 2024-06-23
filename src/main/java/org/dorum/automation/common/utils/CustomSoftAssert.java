package org.dorum.automation.common.utils;

import com.perfecto.reportium.client.ReportiumClient;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.dorum.automation.perfecto.CapabilityName;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@NoArgsConstructor
public class CustomSoftAssert extends SoftAssert {

    private ReportiumClient client;

    public CustomSoftAssert(ReportiumClient client) {
        this.client = client;
    }

    @Override
    public void onAssertFailure(IAssert<?> assertCommand, AssertionError ex) {
        String screenshotName = assertCommand.getMessage();
        if (StringUtils.isEmpty(screenshotName)) {
            screenshotName = "No error message provided";
        }
        storeScreenshot(screenshotName);
//        AppiumCommands.recordPerformanceData();
        client.reportiumAssert(ex.getMessage(), false);
    }

    @Override
    public void onAssertSuccess(IAssert<?> assertCommand) {
        client.reportiumAssert(assertCommand.getMessage(), true);
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
            log.info("Attachment is added to allure report with name: {}", attachmentName);
        } catch (Exception e) {
            log.warn("FAILED - unable to attach attachment {} to RP/Allure report\n{}", attachmentName, e);
        }
    }
}
