package org.dorum.automation.common.utils.perfecto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PerfectoLink {

    ARTIFACTS           ("/repository/api/v1/artifacts"),
    DEVICE_LIST         ("/api/v1/device-management/devices"),
    DEVICE_PARAMETER    ("/services/handsets/%s"),
    EXECUTION           ("/services/executions"),
    EXECUTIONS          ("/services/executions/%s"),
    RESERVATION         ("/api/v1/device-reservation/reservations"),
    RESERVATION_REMOVE  ("/api/v1/device-reservation/reservations/%s");

    private final String url;
    private static final String CLOUD = "cloud.perfectomobile.com";
    private static final String APP_SERVER = "cloud.app.perfectomobile.com";

    public String getUrl() {
        if (url.contains("services")) {
            return String.format("https://%s%s", CLOUD, url);
        }
        return String.format("https://%s%s", APP_SERVER, url);
    }
}
