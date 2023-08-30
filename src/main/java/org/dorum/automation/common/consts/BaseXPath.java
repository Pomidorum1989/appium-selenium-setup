package org.dorum.automation.common.driver.consts;

public class BaseXPath {

    public static final String GENERAL_TEXT = "//*[text()='%s']";
    public static final String GENERAL_CONTAINS_TEXT = "//*[contains(text(), '%s')]";
    public static final String GENERAL_NATIVE_TEXT = "//*[@text='%s']";
    public static final String GENERAL_NATIVE_LABEL = "//*[@label='%s']";
    public static final String GENERAL_NATIVE_VALUE = "//*[@value='%s']";
    public static final String GENERAL_NATIVE_TEXT_OR_LABEL = "//*[@text='%s' or @label='%s']";
    public static final String GENERAL_NATIVE_CONTAINS_TEXT = "//*[contains(@text, '%s')]";
    public static final String GENERAL_NATIVE_CONTAINS_LABEL = "//*[contains(@label, '%s')]";
    public static final String GENERAL_NATIVE_CONTAINS_TEXT_OR_LABEL = "//*[contains(@text, '%s') or contains(@label, '%s')]";
    public static final String GENERAL_CONTAINS_AND_NOT_EQUALS_TEXT =
            "//*[contains(text(), '%s') and not(text()='%s')]";
    public static final String GENERAL_NATIVE_CONTAINS_AND_NOT_EQUALS_TEXT =
            "//*[contains(@text, '%s') and not(@text='%s')]";
    public static final String GENERAL_NATIVE_CONTAINS_AND_NOT_EQUALS_LABEL =
            "//*[contains(@label, '%s') and not(@label='%s')]";
    public static final String GENERAL_NATIVE_CONTAINS_AND_NOT_EQUALS_TEXT_OR_LABEL =
            "//*[((contains(@text, '%s') and not(@text='%s')) or ((contains(@label, '%s') and not(@label='%s'))]";

}
