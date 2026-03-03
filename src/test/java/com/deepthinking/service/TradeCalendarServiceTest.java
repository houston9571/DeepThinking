package com.deepthinking.service;

import com.deepthinking.common.utils.DateUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.deepthinking.common.constant.Constants.ROUND_MODE;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.SIMPLIFIED_CHINESE;

public class TradeCalendarServiceTest {


    @Test
    public void test2() {

    }

    @Test
    public void test() {
        System.out.println("--->" + DateUtils.now().plusDays(7).getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE));
        MathContext mc = new MathContext(4, ROUND_MODE);
        System.out.println(BigDecimal.valueOf(4375378432L).divide(BigDecimal.valueOf(61495128151L), mc));
    }

}
