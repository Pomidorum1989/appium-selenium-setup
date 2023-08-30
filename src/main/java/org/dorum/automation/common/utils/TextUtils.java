package org.dorum.automation.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

  public static final String TECH_SPACE = " ";

  public static String format(String msg, Object... objects) {
    if (StringUtils.isNotEmpty(msg)) {
      try {
        return MessageFormat.format(msg, objects);
      } catch (Exception e) {
        Log.warn("FAILED - MessageFormat\n%s", e);
      }
      msg = msg.replaceAll("\\{[0-9]}", "%s");
      return String.format(msg, objects);
    }
    return "";
  }

  public static String getNumericPartOfText(String text) {
    if (StringUtils.isEmpty(text)) {
      Log.warn("Invalid text - NULL/EMPTY");
      return null;
    }
    if (text.contains(TECH_SPACE)) {
      text = text.replace(TECH_SPACE, " ");
    }
    String result = text.replaceAll("[^\\d.]", "");
    Log.info("Numeric part of text (%s) is: %s", text, result);
    return result;
  }

  public static String getNonNumericPartOfText(String text) {
    if (StringUtils.isEmpty(text)) {
      Log.warn("Invalid text - NULL/EMPTY");
      text = "";
    }
    Pattern pattern = Pattern.compile("[^\\d]+");
    Matcher matcher = pattern.matcher(text);
    StringBuilder stringBuilder = new StringBuilder();
    while (matcher.find()) {
      stringBuilder.append(matcher.group());
    }
    return stringBuilder.toString();
  }

  // timeUnit: min, sec, etc.
  public static int getTimeFromText(String text, String timeUnit) {
    Pattern pattern = Pattern.compile(String.format("(\\d+)(?=\\s*%s)", timeUnit));
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return Integer.parseInt(matcher.group());
    }
    return 0;
  }

  public static String getTimeFromText(String text, String pattern, int index) {
    Pattern regexPattern = Pattern.compile(pattern);
    Matcher matcher = regexPattern.matcher(text);
    String extractedText = "";
    if (matcher.find() && !text.isEmpty()) {
      extractedText = matcher.group(index).trim();
      Log.info("Extracted time: %s", extractedText);
    } else {
      Log.warn("No match found for the patten (%s) from the text: %s", pattern, text);
    }
    return extractedText;
  }

  public static int getDistanceFromText(String text, String measureUnit) {
    Pattern pattern = Pattern.compile(String.format("(\\d+)\\D+%s", measureUnit));
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }
    return 0;
  }

  public static String getArtifactVersion(String input) {
    Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+-\\d+)");
    Matcher matcher = pattern.matcher(input);
    String version = "";
    if (matcher.find()) {
      version = matcher.group(1);
    } else {
      Log.error("No version number found.");
    }
    return version;
  }

  public static String getStringRegex(String text, String pattern, int group) {
    Pattern regexPattern = Pattern.compile(pattern);
    Matcher matcher = regexPattern.matcher(text);
    if (matcher.find() && !text.isEmpty()) {
      return matcher.group(group);
    } else {
      Log.warn("No match found for the patten (%s) from the text: %s", pattern, text);
    }
    return "";
  }

  public static String convertFirstLetterToUppercase(String text) {
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

}
