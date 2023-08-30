package org.dorum.automation.common.utils;

import java.text.DecimalFormat;

public class MathUtils {

    public static int countDelta(int before, int after) {
        int delta = after - before;
        Log.info("Delta is: %s", delta);
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
            Log.warn("FAILED - unable to trim the number %s\n%s", number, e);
        }
        if (number < 0) {
            trimmedNumber = "-" + trimmedNumber;
        }
        return trimmedNumber;
    }
}
