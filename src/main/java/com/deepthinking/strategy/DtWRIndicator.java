package com.deepthinking.strategy;


import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 【东财官方公式版】WR (威廉指标) 计算器
 * <p>
 * 核心算法：严格遵循东财公式 WR = (N日内最高价 - 当日收盘价) / (N日内最高价 - N日内最低价) * 100
 * 取值范围为 0 到 100。
 * WR 越接近 0，表示越超买；WR 越接近 100，表示越超卖。
 */
public class DtWRIndicator extends CachedIndicator<Num> {

    private final HighPriceIndicator highPrice;
    private final LowPriceIndicator lowPrice;
    private final ClosePriceIndicator closePrice;
    private final int barCount; // WR 周期 N

    public DtWRIndicator(BarSeries series, int n) {
        super(series);
        this.highPrice = new HighPriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.barCount = n;
    }

    @Override
    protected Num calculate(int index) {
        HighestValueIndicator highestHigh = new HighestValueIndicator(highPrice, barCount);
        LowestValueIndicator lowestMin = new LowestValueIndicator(lowPrice, barCount);

        Num highestHighPrice = highestHigh.getValue(index);
        Num lowestLowPrice = lowestMin.getValue(index);

        // 按照东财公式计算 WR
        // WR = (N日内最高价 - 当日收盘价) / (N日内最高价 - N日内最低价) * 100
        return ((highestHighPrice.minus(closePrice.getValue(index))).dividedBy(highestHighPrice.minus(lowestLowPrice))).multipliedBy(NUM_100);
    }


    /**
     * 超买区‌：WR ≤ 20‌（表示价格接近N日内最高点，可能回调）
     */
    public boolean isOverbought(int index) {
        Num rsi = getValue(index);
        return rsi != null && rsi.isLessThanOrEqual(NUM_20);
    }

    /**
     * 超卖区‌：WR ≥ 80‌（表示价格接近N日内最低点，可能反弹）
     */
    public boolean isOversold(int index) {
        Num rsi = getValue(index);
        return rsi != null && rsi.isGreaterThanOrEqual(NUM_80);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}