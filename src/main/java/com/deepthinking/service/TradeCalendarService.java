package com.deepthinking.service;

import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.TradeCalendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public interface TradeCalendarService extends MybatisBaseService<TradeCalendar> {


    LocalTime MORNING_OPEN = DateUtils.parseLocalTime("09:30:00", DateFormatEnum.TIME);
    LocalTime MORNING_CLOSE = DateUtils.parseLocalTime("11:30:00", DateFormatEnum.TIME);
    LocalTime AFTERNOON_OPEN = DateUtils.parseLocalTime("13:00:00", DateFormatEnum.TIME);
    LocalTime AFTERNOON_CLOSE = DateUtils.parseLocalTime("15:00:00", DateFormatEnum.TIME);

    Map<String, String> holidays = new HashMap<String, String>() {{
        put("2026-01-01", "元旦");
        put("2026-01-02", "元旦");
        put("2026-01-03", "元旦");
        put("2026-02-17", "春节");
        put("2026-02-18", "春节");
        put("2026-02-19", "春节");
        put("2026-02-20", "春节");
        put("2026-02-21", "春节");
        put("2026-02-22", "春节");
        put("2026-02-23", "春节");
        put("2026-04-04", "清明节");
        put("2026-04-05", "清明节");
        put("2026-04-06", "清明节");
        put("2026-05-01", "劳动节");
        put("2026-05-02", "劳动节");
        put("2026-05-03", "劳动节");
        put("2026-06-19", "端午节");
        put("2026-06-20", "端午节");
        put("2026-06-21", "端午节");
        put("2026-10-01", "国庆节");
        put("2026-10-02", "国庆节");
        put("2026-10-03", "国庆节");
        put("2026-10-04", "国庆节");
        put("2026-10-05", "国庆节");
        put("2026-10-06", "国庆节");
        put("2026-10-07", "国庆节");
    }};

    boolean isTradeDate();

    boolean isTradeDate(LocalDate date);

    boolean isTradeTime();

    boolean isTradeTime(LocalTime time);

    int genYearCalendar();

}