package org.dorum.automation.common.utils;

import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

@Log4j2
public class DateUtils {

    public static final String PATTERN_YMD_HMS_S = "yyyy-MM-dd_HH-mm-ss-SSS";
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    public static final String PATTERN_DMY_HM = "dd-MM-yyyy_hh-mm";
    public static final String PATTERN_MD_HM = "MMddHHmm";
    public static final String PATTERN_HM = "H'hr' mm'min'";
    public static final String PATTERN_M = "mm'min'";
    public static final String FORMATTED_DATE_MESSAGE = "Making formatted date, based on pattern: {}";

    public static String getFormattedDateAsString() {
        return getFormattedDateAsString(PATTERN_YMD_HMS_S);
    }

    public static String getFormattedDateAsString(String pattern) {
        log.debug(FORMATTED_DATE_MESSAGE, pattern);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    public static String getFormattedDateAsString(String pattern, Date date) {
        log.debug(FORMATTED_DATE_MESSAGE, pattern);
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }

    public static Date getStringAsDate(String text) {
        Date date = new Date();
        int attempt = 0;
        String pattern = DateUtils.PATTERN_HM;
        while (attempt < 2) {
            try {
                date = new SimpleDateFormat(pattern).parse(text);
                break;
            } catch (Exception e) {
                log.warn("FAILED - unable to parse string to date with pattern: {}\n{}", pattern, e);
                pattern = PATTERN_M;
                attempt++;
            }
        }
        return date;
    }

    public static Duration getStringAsDuration(String timeString) {
        String[] parts = timeString.trim().split("\\s+");
        int hours = 0;
        int minutes = 0;
        for (String part : parts) {
            if (part.endsWith("hr")) {
                String hoursString = part.substring(0, part.length() - 2);
                hours = Integer.parseInt(hoursString);
            } else if (part.endsWith("min")) {
                String minutesString = part.substring(0, part.length() - 3);
                minutes = Integer.parseInt(minutesString);
            }
        }
        return Duration.ofHours(hours).plusMinutes(minutes);
    }

    public static int getDateAsNumber() {
        LocalDate currentDate = LocalDate.now();
        int day = currentDate.getDayOfMonth();
        Month month = currentDate.getMonth();
        int year = currentDate.getYear();
        return Integer.parseInt(day + String.valueOf(month.getValue()) + year);
    }

    public static Date getDateFromEpoch(long milliSeconds) {
        Instant instant = Instant.ofEpochMilli(milliSeconds);
        return Date.from(instant);
    }

    public static long getCurrentEpochTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime roundedTime = currentTime.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
        return roundedTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
