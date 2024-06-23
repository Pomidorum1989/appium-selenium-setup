package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectConfig {

    ANDROID_DEVICE_ID           ("android.device.id"),
    APPLICATION_VERSION         ("application.version"),
    BROWSER_SIZE                ("selenium.browser.size"),
    DB_HOST                     ("db.host"),
    DB_PASSWORD                 ("db.password"),
    DB_USER_NAME                ("db.user.name"),
    DRIVER_NAME                 ("driver.name"),
    EXACT_VERSION               ("exact.version"),
    IS_RELEASE_BUILD            ("is.release.build"),
    HTTPS_SCHEMA                ("schema.value.https"),
    HOST                        ("host.value"),
    IS_CLEAR_APP                ("is.clear.app"),
    IS_EXACT_DEVICE             ("is.exact.device"),
    IS_EXACT_VERSION            ("is.exact.version"),
    IS_NETWORK_VIRTUALIZATION   ("is.network.virtualization"),
    IS_MAXIMIZED                ("selenium.start.maximized"),
    IS_REMOTE                   ("selenium.remote"),
    IOS_DEVICE_ID               ("ios.device.id"),
    MOBILE_PORT                 ("port.value"),
    OATH_TOKEN_PERFECTO_DORUM   ("perfecto.oath.token.dorum"),
    PERFECTO_STATUS             ("perfecto.status"),
    PERFECTO_URL                ("perfecto.url"),
    PROXY_HOST                  ("proxy.host"),
    PROXY_PORT                  ("proxy.port"),
    PROXY_PASS                  ("proxy.pass"),
    PROXY_USER                  ("proxy.user"),
    SESSION_ID                  ("session.id");

    private final String configName;
}
