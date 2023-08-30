package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllureReportParameter {

    APPLICATION_VERSION      ("Application version"),
    DEVICE_ID                ("Device id"),
    DEVICE_LANGUAGE          ("Device language"),
    DEVICE_LOCATION          ("Device location"),
    DEVICE_MODEL             ("Device model"),
    DEVICE_PLATFORM          ("Device platform"),
    EXECUTION_ID             ("Execution id"),
    NETWORK_STATUS           ("Network status"),
    PERFECTO_PDF_REPORT_LINK ("Perfecto pdf report link"),
    PERFECTO_REPORT_LINK     ("Perfecto report link"),
    PERFECTO_SESSION_ID      ("Perfecto session id"),
    PLATFORM_VERSION         ("Platform version");

    private final String value;
}
