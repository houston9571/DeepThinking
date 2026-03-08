package com.deepthinking.service;

import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.TradeCalendar;

public interface TradeCalendarService extends MybatisBaseService<TradeCalendar> {


    int genYearCalendar();

}