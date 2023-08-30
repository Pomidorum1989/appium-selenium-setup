package org.dorum.automation.common.utils.perfecto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerfectoDeviceParam {

    MODEL           ("handset.model",      "Device model"),
    LOCATION        ("handset.location",   "Device location"),
    LANGUAGE        ("handset.language",   "Device language"),
    AVAILABILITY    ("handset.available",  "Device availability"),
    RESERVED        ("handset.reserved",   "Device reservation"),
    RESERVED_TO     ("handset.reservedTo", "Device reservation by user"),
    OS_VERSION      ("handset.osVersion",  "Device OS version");

    private final String parameter, comment;
}
