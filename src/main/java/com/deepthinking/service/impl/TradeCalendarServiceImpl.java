package com.deepthinking.service.impl;

import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.TradeCalendar;
import com.deepthinking.mysql.mapper.TradeCalendarMapper;
import com.deepthinking.service.TradeCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.common.constant.Constants.DISABLED;
import static com.deepthinking.common.constant.Constants.ENABLE;
import static com.deepthinking.common.enums.DateFormatEnum.DATE;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.SIMPLIFIED_CHINESE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeCalendarServiceImpl extends MybatisBaseServiceImpl<TradeCalendarMapper, TradeCalendar> implements TradeCalendarService {


    private final TradeCalendarMapper tradeCalendarMapper;


    public boolean isTradeDate() {
        return isTradeDate(LocalDate.now());
    }

    public boolean isTradeDate(LocalDate date) {
        return (date.getDayOfWeek().getValue() <= 5) && !holidays.containsKey(DateUtils.format(date, DATE));
    }

    public boolean isTradeTime() {
        return isTradeTime(LocalTime.now());
    }

    public boolean isTradeTime(LocalTime time) {
        return isTradeDate() && (time.isAfter(MORNING_OPEN) && time.isBefore(MORNING_CLOSE)) || (time.isAfter(AFTERNOON_OPEN) && time.isBefore(AFTERNOON_CLOSE));
    }

    public int genYearCalendar() {
        LocalDate b = DateUtils.parseLocalDate(DateUtils.now().getYear() + "-01-01", DATE);
        LocalDate e = DateUtils.parseLocalDate((DateUtils.now().getYear() + 1) + "-01-01", DATE);
        List<TradeCalendar> list = new ArrayList<>(366);
        while (b.isBefore(e)) {
            list.add(TradeCalendar.builder()
                    .date(b)
                    .week(b.getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE))
                    .isTrade((b.getDayOfWeek().getValue() <= 5) && !holidays.containsKey(DateUtils.format(b, DATE)) ? ENABLE : DISABLED)
                    .holiday(holidays.getOrDefault(DateUtils.format(b, DATE), ""))
                    .build());
            b = b.plusDays(1);
        }
        return saveOrUpdateBatch(list, new String[]{"date"});
    }

}
