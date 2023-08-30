package org.dorum.automation.common.utils.enums;

import io.appium.java_client.remote.MobilePlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dorum.automation.common.utils.appium.AppiumCommands;

@Getter
@AllArgsConstructor
public enum MobileContext {

    NATIVE          ("NATIVE",        "NATIVE_APP"),
    WEBVIEW         ("WEBVIEW", String.format("WEBVIEW_%s", "packageName")),
    WEBVIEW_CHROME  ("WEBVIEW_CHROME", "WEBVIEW_chrome"),
    WEBVIEW_DEBUG   ("WEBVIEW_DEBUG", "WEBVIEW_1");

    private final String contextName, contextValue;

    @Override
    public String toString() {
        return contextName;
    }

    public String getWebviewContent(MobilePlatform platform, TitleName titleName) {
        if (platform.equals(MobilePlatform.IOS)) {;
            return contextValue.replace("packageName", AppiumCommands.getIosWebviewContext(titleName));
        } else {
            return contextValue;
        }


    }
}
