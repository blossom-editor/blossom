package com.blossom.common.base.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Calendar;
import java.util.Date;


/**
 * @author xzzz
 * @since 0.0.1
 */
public class DateUtils extends DateUtil {

    public static String PATTERN_YYYYMM = DatePattern.NORM_MONTH_PATTERN;
    public static String PATTERN_YYYYMMDD = DatePattern.NORM_DATE_PATTERN;
    public static String PATTERN_YYYYMMDDHHMMSS = DatePattern.NORM_DATETIME_PATTERN;
    public static String PATTERN_YYYYMMDDHHMMSS_SSS = DatePattern.NORM_DATETIME_MS_PATTERN;

    /**
     * 将字符串格式的日期转为时间戳(毫秒)
     *
     * @param date 日期
     * @return 时间戳
     */
    public static long toTimestamp(String date) {
        return toTimestamp(DateUtils.parse(date));
    }

    /**
     * 将Date格式的日期转为时间戳(毫秒)
     *
     * @param date 日期
     * @return 时间戳
     */
    public static long toTimestamp(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    /**
     * 将日期转换为 yyyy-MM-dd
     *
     * @param date 日期
     * @return yyyy-MM-dd
     */
    public static String toYMD(Date date) {
        return DateUtils.format(date, DatePattern.NORM_DATE_PATTERN);
    }


    /**
     * 将日期转换为 yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String toYMDHMS(Date date) {
        return DateUtils.format(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 时间戳换为 yyyy-MM-dd HH:mm:ss
     *
     * @param timestamp 时间戳
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String toYMDHMS_SSS(Long timestamp) {
        return DateUtils.format(date(timestamp), DatePattern.NORM_DATETIME_MS_PATTERN);
    }
}
