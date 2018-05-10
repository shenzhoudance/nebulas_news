package com.nebulas.io.util;

import java.util.Calendar;

/**
 * Created by nebulas on 2018/5/10.
 */

public class TimeUtils {

    private static final String SEP = "-";
    private static final String SEP_COLON = ":";

    /**
     * 格式化时间戳为'yyyy-M-d'格式的日期字符串
     *
     * @param millis 毫秒
     */
    public static String formatDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) % 12 + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StringBuilder sb = new StringBuilder();
        sb.append(year).append(SEP).append(month).append(SEP).append(day);
        return sb.toString();
    }

    /**
     * 格式化时间戳为'yyyy-M-d HH:mm'格式的日期字符串
     *
     * @param millis
     */
    public static String formatTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) % 12 + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        StringBuilder sb = new StringBuilder();
        sb.append(year).append(SEP).append(month).append(SEP).append(day)
                .append(" ").append(getReadableTimeField(hour))
                .append(SEP_COLON).append(getReadableTimeField(min));
        return sb.toString();
    }

    private static String getReadableTimeField(int field) {
        if (field < 10) {
            return "0" + field;
        } else {
            return "" + field;
        }
    }
}
