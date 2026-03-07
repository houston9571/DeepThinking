package com.deepthinking.strategy;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.deepthinking.strategy.StrategyUtils.NUM_0;
import static com.deepthinking.strategy.StrategyUtils.NUM_100;
import static org.ta4j.core.num.DoubleNum.HUNDRED;

/**
 * 【东财官方公式版】BIAS 计算器
 *
 * 核心算法：严格遵循东财公式 BIAS = (当日收盘价 - N日移动平均价) / N日移动平均价 * 100
 * 为确保精度，内部使用 BigDecimal 进行计算。
 *
 * 为了与东财完全对齐，N日移动平均价必须与东财使用的方法完全一致。
 * 此处采用最通用的标准移动平均 (SMA)。
 */
public class DtBIASIndicator extends CachedIndicator<Num> {

    private final ClosePriceIndicator closePrice;
    private final SMAIndicator smaIndicator;                    // N日移动平均线

    public DtBIASIndicator(BarSeries series, int n) {
        super(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.smaIndicator = new SMAIndicator(closePrice, n);    // 使用标准SMA
    }

    @Override
    protected Num calculate(int index) {
        // 获取当日收盘价
        Num currentClose = closePrice.getValue(index);
        // 获取 N 日移动平均价
        Num nDayAverage = smaIndicator.getValue(index);
        // 如果移动平均价为0，则BIAS无意义，返回0或null
        if (nDayAverage.isZero()) {
            return NUM_0;
        }
        // 按照东财公式计算 BIAS
        // BIAS = (当日收盘价 - N日移动平均价) / N日移动平均价 * 100
        Num numerator = currentClose.minus(nDayAverage);
        return numerator.dividedBy(nDayAverage).multipliedBy(NUM_100);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}