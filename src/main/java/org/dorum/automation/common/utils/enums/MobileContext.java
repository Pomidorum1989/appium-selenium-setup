package org.dorum.automation.common.utils.enums;

import io.appium.java_client.remote.MobilePlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dorum.automation.common.utils.appium.AppiumCommands;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MobileContext {

    NATIVE          ("NATIVE",        "NATIVE_APP"),
    WEBVIEW         ("WEBVIEW", String.format("WEBVIEW_%s", "packageName")),
    WEBVIEW_CHROME  ("WEBVIEW_CHROME", "WEBVIEW_chrome"),
    WEBVIEW_DEBUG   ("WEBVIEW_DEBUG", "WEBVIEW_1");

    private final String contextName, contextValue;

}
