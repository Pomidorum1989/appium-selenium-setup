package org.dorum.automation.common.utils;

import lombok.extern.log4j.Log4j2;

import java.text.DecimalFormat;

@Log4j2
public class MathUtils {

    public static int countDelta(int before, int after) {
        int delta = after - before;
        log.info("Delta is: {}", delta);
        return Math.abs(delta);
    }

    public static String trimDecimal(Double number) {
        return trimDecimal(number, 5);
    }

    public static String trimDecimal(Double number, int numberOfChars) {
        Double module = Math.abs(number);
        String builder = "#." + "#".repeat(Math.max(0, numberOfChars));
        DecimalFormat decimalFormat = new DecimalFormat(builder);
        String trimmedNumber = "";
        try {
            trimmedNumber = decimalFormat.format(module);
        } catch (Exception e) {
            log.warn("FAILED - unable to trim the number {}\n{}", number, e);
        }
        if (number < 0) {
            trimmedNumber = "-" + trimmedNumber;
        }
        return trimmedNumber;
    }
}
