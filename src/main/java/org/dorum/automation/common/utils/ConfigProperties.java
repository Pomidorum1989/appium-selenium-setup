package org.dorum.automation.common.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.dorum.automation.common.utils.enums.ProjectConfig;

import java.nio.file.Path;
import java.util.Properties;

@Log4j2
public class ConfigProperties {

    private static final Properties properties = new Properties();
    private static Path configPath;

    public static void loadProperties(Path configFilePath) {
        try {
            String fileName = configFilePath.getFileName().toString();
            if (fileName.toLowerCase().contains("common")) {
                configPath = configFilePath;
            }
            properties.load(FileUtils.class.getClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            log.error("FAILED - load properties file\n{}", e);
        }
    }

    public static String getProperty(ProjectConfig configValue) {
        return properties.getProperty(configValue.getConfigName());
    }

    public static void setProperties(ProjectConfig configValue, String value) {
        properties.setProperty(configValue.getConfigName(), value);
        try {
            int position = DataUtils.findLineNumber(configPath.toAbsolutePath().toString(), configValue.getConfigName());
            DataUtils.overwriteLine(configPath, configValue.getConfigName() + "=" + value, position - 1);
        } catch (Exception e) {
            log.warn("Failed to replace %s property in config\n{}", configValue, e);
        }
    }
}
