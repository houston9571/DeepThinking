package com.deepthinking.strategy;

import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface StrategyUtils {


    Num NUM_0 = DecimalNum.valueOf(0);
    Num NUM_1 = DecimalNum.valueOf(1);
    Num NUM_2 = DecimalNum.valueOf(2);
    Num NUM_10 = DecimalNum.valueOf(10);
    Num NUM_20 = DecimalNum.valueOf(20);
    Num NUM_30 = DecimalNum.valueOf(20);
    Num NUM_50 = DecimalNum.valueOf(50);
    Num NUM_80 = DecimalNum.valueOf(80);
    Num NUM_90 = DecimalNum.valueOf(90);
    Num NUM_100 = DecimalNum.valueOf(100);

    Num NONE = DecimalNum.valueOf(0);

    Num GOLDEN_CROSS_RED = DecimalNum.valueOf(2);
    Num GOLDEN_CROSS = DecimalNum.valueOf(1);
    Num DEATH_CROSS = DecimalNum.valueOf(-1);
    Num DEATH_CROSS_GREEN = DecimalNum.valueOf(-2);

      static Num numOf(Number n){
        return DecimalNum.valueOf(n);
    }


    /**
     * 东财风格数值格式化（银行家舍入法）
     */
      static double formatNum(double num, int scale) {
        return new BigDecimal(num).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
    }
}
