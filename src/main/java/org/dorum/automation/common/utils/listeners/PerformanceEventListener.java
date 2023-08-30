package org.dorum.automation.common.utils.listeners;

import lombok.NoArgsConstructor;
import org.dorum.automation.common.utils.appium.AppiumCommands;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

@NoArgsConstructor
public class PerformanceEventListener extends AbstractWebDriverEventListener {

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {
        AppiumCommands.recordPerformanceData();
    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {
        AppiumCommands.recordPerformanceData();
    }

}
