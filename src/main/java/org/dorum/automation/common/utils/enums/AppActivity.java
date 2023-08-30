package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppActivity {

    ANDROID_MAP_ACTIVITY        ("com.google.android.maps.MapsActivity"),
    BLUETOOTH_ACTIVITY          ("android.settings.BLUETOOTH_SETTINGS"),
    CHROME_ACTIVITY             ("com.google.android.apps.chrome.Main");


    private final String value;
}
