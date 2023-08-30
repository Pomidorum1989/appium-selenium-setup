package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectConfig {

    ANDROID_DEVICE_ID           ("android.device.id"),
    ANDROID_INSTALLED_VERSION   ("android.installed.version"),
    ANDROID_RESERVATION_ID      ("android.reservation.id"),
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
    FAKE_PAPI_URL               ("fake.papi.url"),
    IS_CLEAR_APP                ("is.clear.app"),
    IS_EXACT_DEVICE             ("is.exact.device"),
    IS_EXACT_VERSION            ("is.exact.version"),
    IS_NETWORK_VIRTUALIZATION   ("is.network.virtualization"),
    IS_MAXIMIZED                ("selenium.start.maximized"),
    IS_REMOTE                   ("selenium.remote"),
    IOS_DEVICE_ID               ("ios.device.id"),
    IOS_INSTALLED_VERSION       ("ios.installed.version"),
    IOS_RESERVATION_ID          ("ios.reservation.id"),
    MOBILE_PORT                 ("port.value"),
    OATH_TOKEN_PERFECTO_DORUM   ("perfecto.oath.token.dorum"),
    PERFECTO_STATUS             ("perfecto.status"),
    PERFECTO_URL                ("perfecto.url"),
    PROXY_HOST_DC               ("proxy.host.dc"),
    PROXY_PORT_DC               ("proxy.port.dc"),
    PROXY_HOST_NA               ("proxy.host.na"),
    PROXY_PORT_NA               ("proxy.port.na"),
    PROXY_PASS_NA               ("proxy.pass.na"),
    PROXY_USER_NA               ("proxy.user.na"),
    RUN_BY_JENKINS              ("is.run.as.jenkins"),
    SESSION_ID                  ("session.id"),
    VCN_ENVIRONMENT             ("vcn.environment");

    private final String configName;
}
