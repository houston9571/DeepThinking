package com.deepthinking.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 【东财专用版】EMA 计算器
 * <p>
 * 核心逻辑对齐东方财富 APP：
 * 1. 初始值：EMA[0] = Close[0] (直接使用第一根 K 线的收盘价)
 * 2. 递推：EMA[i] = Close[i] * (2/(N+1)) + EMA[i-1] * (1 - 2/(N+1))
 * <p>
 * ⚠️ 注意：此算法在前 N 天内与学术定义 (SMA 种子) 不同，但与国内所有行情软件一致。
 */
public class DtEMAIndicator extends CachedIndicator<Num> {

    private final Indicator<Num> indicator;
    private final int barCount; // 周期 N
    private List<Num> emaValues;

    public DtEMAIndicator(Indicator<Num> indicator, int n) {
        super(indicator);
        this.indicator = indicator;
        this.barCount = n;
        preCalculate();
    }

    private void preCalculate() {
        int seriesLength = getBarSeries().getBarCount();
        emaValues = new ArrayList<>(seriesLength);

        if (seriesLength == 0) return;

        // 1. 计算平滑系数 alpha = 2 / (N + 1)
        Num alpha = NUM_2.dividedBy(numOf(barCount + 1));
        Num oneMinusAlpha = NUM_1.minus(alpha);

        // 2. 初始化：EMA[0] = Close[0] (东财核心逻辑)
        Num currentEma = indicator.getValue(0);
        emaValues.add(currentEma);

        // 3. 递推计算
        for (int i = 1; i < seriesLength; i++) {
            Num close = indicator.getValue(i);
            // EMA = Close * alpha + PrevEMA * (1 - alpha)
            currentEma = close.multipliedBy(alpha).plus(currentEma.multipliedBy(oneMinusAlpha));
            emaValues.add(currentEma);
        }
    }

    @Override
    protected Num calculate(int index) {
        return emaValues.get(index);
    }


    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    /**
     * 获取整个序列的 EMA 列表 (用于调试或批量计算)
     */
    public List<Num> getAllValues() {
        return emaValues;
    }
}