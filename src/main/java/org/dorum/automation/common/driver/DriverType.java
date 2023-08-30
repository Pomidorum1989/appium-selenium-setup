package org.dorum.automation.common.driver;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum DriverType {

    CHROME("Chrome") {
        @Override
        public AbstractDriverManager getDriverManager() {
            return ChromeDriverManager.getInstance();
        }
    },
    ANDROID("Android") {
        @Override
        public AbstractDriverManager getDriverManager() {
            return AndroidDriverManager.getInstance();
        }
    },
    IOS("iOS") {
        @Override
        public AbstractDriverManager getDriverManager() {
            return IosDriverManager.getInstance();
        }
    };

    private final String name;

    public abstract AbstractDriverManager getDriverManager();

    public static DriverType getDriverByName(String driverName) {
        return Stream.of(values()).filter(driver -> driver.getName().equalsIgnoreCase(driverName)).findFirst()
                .orElse(CHROME);
    }
}
