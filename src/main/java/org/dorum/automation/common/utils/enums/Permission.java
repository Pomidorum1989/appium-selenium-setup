package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {

    ACCESS_COARSE_LOCATION  ("android.permission.ACCESS_COARSE_LOCATION"),
    ACCESS_FINE_LOCATION    ("android.permission.ACCESS_FINE_LOCATION"),
    BLUETOOTH_CONNECT       ("android.permission.BLUETOOTH_CONNECT"),
    BLUETOOTH_SCAN          ("android.permission.BLUETOOTH_SCAN"),
    CAMERA                  ("android.permission.CAMERA"),
    CAR_VENDOR_EXTENSION    ("com.google.android.gms.permission.CAR_VENDOR_EXTENSION"),
    POST_NOTIFICATIONS      ("android.permission.POST_NOTIFICATIONS"),
    READ_CALENDAR           ("android.permission.READ_CALENDAR"),
    READ_CONTACTS           ("android.permission.READ_CONTACTS"),
    READ_PHONE_STATE        ("android.permission.READ_PHONE_STATE"),
    READ_STORAGE            ("android.permission.READ_EXTERNAL_STORAGE"),
    RECORD_AUDIO            ("android.permission.RECORD_AUDIO"),
    WRITE_CALENDAR          ("android.permission.WRITE_CALENDAR"),
    WRITE_CONTACTS          ("android.permission.WRITE_CONTACTS"),
    WRITE_STORAGE           ("android.permission.WRITE_EXTERNAL_STORAGE");

    private final String command;
}
