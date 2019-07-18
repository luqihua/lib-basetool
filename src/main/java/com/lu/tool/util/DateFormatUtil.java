package com.lu.tool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created  on 2017/3/20.
 * by luqihua
 */

public class DateFormatUtil {
    private static String DEFAULT_FORMAT_MODE = "yyyy-MM-dd";//默认的解析/格式化模式

    /**
     * 时间戳格式化成对应的字符串
     *
     * @param millisTime 时间戳
     * @param formatMode
     * @return
     */
    public static String formatTimeMillis(long millisTime, String formatMode) {
        final Date date = new Date(millisTime);
        SimpleDateFormat format = new SimpleDateFormat(formatMode, Locale.CHINA);
        return format.format(date);
    }

    /**
     * 时间戳格式化成对应的字符串
     */
    public static String formatTimeMillis(String timeMillis, String formatMode) {
        return formatTimeMillis(Long.valueOf(timeMillis), formatMode);
    }

    /**
     * 时间戳格式化成对应的字符串
     * 使用默认的格式
     */
    public static String formatTimeMillis(long timeMillis) {
        return formatTimeMillis(timeMillis, DEFAULT_FORMAT_MODE);
    }

    /**
     * 时间戳格式化成对应的字符串
     * 使用默认的格式
     */
    public static String formatTimeMillis(String timeMillis) {
        return formatTimeMillis(Long.valueOf(timeMillis), DEFAULT_FORMAT_MODE);
    }

    /**
     * 字符串解析成日期
     *
     * @param dateStr
     * @param parseMode
     * @return
     */
    public static Date str2Date(String dateStr, String parseMode) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(parseMode, Locale.CHINA);
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用默认的解析模式解析
     *
     * @param dateStr
     * @return
     */
    public static Date str2Date(String dateStr) {
        return str2Date(dateStr, DEFAULT_FORMAT_MODE);
    }
}
