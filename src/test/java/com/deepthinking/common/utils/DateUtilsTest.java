package com.deepthinking.common.utils;

import com.google.common.collect.Maps;
import com.deepthinking.BaseTest;
import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.thread.Threads;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

public class DateUtilsTest extends BaseTest {


    @Test
    public void loop() {
        LocalDateTime repeatTime = null;
        do {
            LocalDateTime ld = DateUtils.now();
            int t = ld.getMinute();
            if (t % 2 == 0) {
                System.out.println(t + "                                                  self  " + DateUtils.format(ld.minusMinutes(10)) + "  " + DateUtils.format(ld));
            } else {
                if (repeatTime == null || repeatTime.isAfter(ld)) {
                    repeatTime = ld.minusMinutes(120);
                    System.out.println("_________________________________________________________");
                }
                System.out.println(t + " repeat  " + DateUtils.format(repeatTime.minusMinutes(10)) + "  " + DateUtils.format(repeatTime));
                repeatTime = repeatTime.plusMinutes(10);
            }
            Threads.sleep(1000 * 60);
        } while (true);
    }

    @Test
    public void loop2() {
        LocalDateTime repeatTime = null;
        do {
            LocalDateTime ld = DateUtils.now();
            int t = ld.getSecond();
            if (t % 10 == 0) {
                System.out.println(t + "                                                  repeat  " + DateUtils.format(ld.minusMinutes(10)) + "  " + DateUtils.format(ld));
            } else {
                System.out.println(t + " self  " + DateUtils.format(ld) + "  " + DateUtils.format(ld));
            }
            Threads.sleep(1000);
        } while (true);
    }

    @Test
    public void en() {
        for (int i = 0; i < 60; i++) {
            if (i % 10 == 0) {
                System.out.println(i);
            }
        }

//        System.out.println(DateUtils.getMonthStartTimeStr(LocalDate.now()));
//        System.out.println(DateUtils.getMonthEndTimeStr(LocalDate.now()));
//        System.out.println(DateUtils.getTimeOfDay());
//        System.out.println(DateUtils.getNextHours());
        System.out.println(DateUtils.format(DateUtils.toLocalDateTime(1709520120489L), DateFormatEnum.TIMESTAMP));
        System.out.println(DateUtils.format(DateUtils.toLocalDateTime(1709520119502L), DateFormatEnum.TIMESTAMP));
        System.out.println(DateUtils.toSystemZone("2024-08-10 04:12:17"));
//        System.out.println(DateUtils.toUTC("2024-02-05 03:13:15"));
    }

    @Test
    public void randomNumber() {
        Map<String, Integer> map = Maps.newHashMap();
        int c = 0;
        for (int i = 0; i < 1000000; i++) {
            String nm = NumberUtils.randomStr(8);
            if (map.containsKey(nm)) {
                c++;
            } else {
                map.put(nm, 1);
            }
        }
        System.out.println(" 重复：" + c);


    }


    @Test
    public void zone() {
        System.out.println(DateUtils.now().getMinute() - 1);
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));

//        System.out.println(ZoneId.systemDefault());
        System.out.println(DateUtils.parse("2024-10-19 16:19:00").atZone(UTC).toOffsetDateTime().withOffsetSameInstant(UTC).toString());
        System.out.println(DateUtils.parse(("2024-10-19 16:19:00")).atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).toInstant().atZone(ZoneId.of("GMT-4")).toLocalDateTime());
        System.out.println(DateUtils.format(DateUtils.systemZoneToZone("2024-10-19 17:27:00", ZoneId.of("GMT-4"))));

        System.out.println(DateUtils.parse(("2024-10-21 00:34:51")).atZone(ZoneId.of("GMT-4")).withZoneSameInstant(UTC).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        System.out.println(DateUtils.toLocalDateTime(1729571267000L));
        // 2024-10-21 12:35:01

    }


    @Test
    public void dateToTimeStamp() {
        long s = DateUtils.toEpochMilli(LocalDateTime.now());
        System.out.println(s);
        System.out.println(DateUtils.getStartTimeStr(LocalDate.now()));
        System.out.println(DateUtils.getEndTimeStr(LocalDate.now()));
        System.out.println(DateUtils.getMonthStartTimeStr(LocalDate.now()));
        System.out.println(DateUtils.getMonthEndTimeStr(LocalDate.now()));
        System.out.println();
        System.out.println(DateUtils.toUTC(DateUtils.getStartTimeStr(LocalDate.now())));
        System.out.println(DateUtils.toUTC(DateUtils.getEndTimeStr(LocalDate.now())));
        System.out.println(DateUtils.toUTC(DateUtils.getMonthStartTimeStr(LocalDate.now())));
        System.out.println(DateUtils.toUTC(DateUtils.getMonthEndTimeStr(LocalDate.now())));
    }


    @Test
    public void crossYear() {
        LocalDate start = LocalDate.now();
        for (int i = 0; i < 100; i++) {
            start = start.plusDays(1);
            LocalDate f = start.minusMonths(7);
            LocalDate t = start.minusMonths(6).minusDays(1);
            if (f.getYear() == t.getYear()) {
                System.out.println(DateUtils.getStartTimeStr(f) + "   " + DateUtils.getEndTimeStr(t));
            } else {
                LocalDate yearEnd = LocalDate.of(f.getYear(), 12, 31);
                System.out.print(DateUtils.getStartTimeStr(f) + "   " + DateUtils.getEndTimeStr(yearEnd) + " >> ");
                LocalDate yearStart = LocalDate.of(t.getYear(), 1, 1);
                System.out.println(DateUtils.getStartTimeStr(yearStart) + "   " + DateUtils.getEndTimeStr(t));
            }
        }
    }
}
