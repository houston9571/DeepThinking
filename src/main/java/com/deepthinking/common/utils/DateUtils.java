package com.deepthinking.common.utils;

import com.deepthinking.common.enums.DateFormatEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.deepthinking.common.enums.DateFormatEnum.*;
import static java.time.ZoneOffset.UTC;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.SIMPLIFIED_CHINESE;

@Slf4j
public class DateUtils {


    /**
     * yyyy-MM-dd HH:mm:ss -> YYYY-MM-DDTHH:mm:ss.sssZ
     * mongodb GMT -> UTC
     */
    public static String toUTC(String date) {
        if (date.length() > 20) {
            return parse(date, DateFormatEnum.TIMESTAMP).atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(DateTimeFormatter.ofPattern(TIMESTAMP_Z.getFormat()));
        } else {
            return parse(date).atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).format(DateTimeFormatter.ofPattern(DATETIME_Z.getFormat()));
        }
    }

    public static LocalDateTime systemZoneToZone(String date, ZoneId zoneId) {
        return parse(date).atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).toInstant().atZone(zoneId).toLocalDateTime();
    }


    public static LocalDateTime zoneToSystemZone(String date, ZoneId zoneId) {
        return parse(date).atZone(zoneId).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime toSystemZone(String date) {
        return parse(date).atZone(UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime toSystemZone(String date, DateFormatEnum df) {
        return parse(date, df).atZone(UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static long toEpochMilli(LocalDateTime date) {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static int toEpochSecond(LocalDateTime date) {
        return (int) date.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     */
    public static LocalDateTime parse(String date) {
        return parse(date, DATETIME);
    }

    /**
     * 指定格式format日期
     */
    public static LocalDateTime parse(String date, DateFormatEnum df) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(df.getFormat()));
    }

    public static LocalDate parseLocalDate(String date, DateFormatEnum df) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(df.getFormat()));
    }

    public static LocalTime parseLocalTime(String time, DateFormatEnum df) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(df.getFormat()));
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat(DATETIME.getFormat()).parse(date);
        } catch (ParseException e) {
            log.error("parseDate date={}", date, e);
            return new Date();
        }
    }

    /**
     * 指定格式format日期
     */
    public static String format(String date, DateFormatEnum df) {
        return format(parse(date), df);
    }

    /**
     * 默认格式"yyyy-MM-dd HH:mm:ss"
     */
    public static String format(LocalDateTime date) {
        return format(date, DATETIME);
    }

    public static String format(LocalDateTime date, DateFormatEnum df) {
        return date.format(DateTimeFormatter.ofPattern(df.getFormat()));
    }

    public static String format(LocalDate date, DateFormatEnum df) {
        return date.format(DateTimeFormatter.ofPattern(df.getFormat()));
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String now(DateFormatEnum df) {
        return format(now(), df != null ? df : DATETIME);
    }


    /****
     * 获取当天（按当前传入的时区）00:00:00所对应时刻的long型值
     */
    public static long getTimeOfDay() {
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取下一个小时时间戳
     *
     * @return
     */
    public static long getNextHours() {
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return calendar.getTimeInMillis();
    }


    /**
     * 获取天开始时间
     */
    public static String getStartTimeStr(LocalDate date) {
        LocalDateTime todayStart = LocalDateTime.of(date, LocalTime.MIN);
        return todayStart.format(DateTimeFormatter.ofPattern(DateFormatEnum.TIMESTAMP.getFormat()));
    }

    /**
     * 获取天结束时间
     */
    public static String getEndTimeStr(LocalDate date) {
        LocalDateTime todayEnd = LocalDateTime.of(date, LocalTime.MAX);
        return todayEnd.format(DateTimeFormatter.ofPattern(DateFormatEnum.TIMESTAMP.getFormat()));
    }

    /**
     * 获取月第一天
     */
    public static String getMonthStartTimeStr(LocalDate date) {
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00.000";
    }

    /**
     * 获取月最后一天
     */
    public static String getMonthEndTimeStr(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59.999";
    }

    public static String getShortWeekName(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE);
    }

    /****
     * 获取1天剩余的秒数
     */
    public static long getRemainSecondsOneDay(LocalDateTime currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(UTC), ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(UTC), ZoneId.systemDefault());
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }

    public LocalDateTime timestampToDatetime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     */
    public static String formatDateTime(long timeMillis) {
        long sec = timeMillis / 1000;
        long day = sec / (24 * 60 * 60);
        long hour = sec / (60 * 60) - day * 24;
        long min = (sec / 60) - day * 24 * 60 - hour * 60;
        long s = sec - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
        return (day > 0 ? day + " days, " : "") + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", s) + "." + String.format("%03d", (timeMillis % 1000));
    }

}
