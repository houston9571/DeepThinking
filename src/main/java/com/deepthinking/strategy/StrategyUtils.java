package com.deepthinking.strategy;

import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public interface StrategyUtils {


    Num NUM_0 = DecimalNum.valueOf(0);
    Num NUM_1 = DecimalNum.valueOf(1);
    Num NUM_2 = DecimalNum.valueOf(2);
    Num NUM_10 = DecimalNum.valueOf(10);
    Num NUM_20 = DecimalNum.valueOf(20);
    Num NUM_30 = DecimalNum.valueOf(20);
    Num NUM_50 = DecimalNum.valueOf(50);
    Num NUM_70 = DecimalNum.valueOf(70);
    Num NUM_80 = DecimalNum.valueOf(80);
    Num NUM_90 = DecimalNum.valueOf(90);
    Num NUM_100 = DecimalNum.valueOf(100);


    // 背离类型常量
    public enum DivergenceType {
        NONE, TOP, BOTTOM;
    }


    public enum SignalType {
        BUY, SELL, WATCH;
    }

    public enum SignalLevel {
        NONE, WEAK, LOW, MEDIUM, HIGH, HIGHEST;
    }

    static Num numOf(Number n) {
        return DecimalNum.valueOf(n);
    }


    /**
     * 东财风格数值格式化（银行家舍入法）
     */
    static double formatNum(double num, int scale) {
        return new BigDecimal(num).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
    }


    static boolean isHighestNum(List<Num> list, Num num) {
        for (Num n : list) {
            if (n != null && n.isGreaterThan(num)) {
                return false;
            }
        }
        return true;
    }

    static boolean isLowestNum(List<Num> list, Num num) {
        for (Num n : list) {
            if (n != null && n.isLessThan(num)) {
                return false;
            }
        }
        return true;
    }
}
