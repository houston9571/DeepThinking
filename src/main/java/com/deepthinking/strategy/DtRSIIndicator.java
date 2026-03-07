package com.deepthinking.strategy;

import com.google.common.collect.Lists;
import org.apache.bcel.generic.RET;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.common.constant.Constants.HUNDRED;
import static com.deepthinking.common.constant.Constants.ROUND_MODE;
import static com.deepthinking.strategy.StrategyUtils.*;
import static java.lang.Double.PRECISION;

/**
 * 【东财专用版】RSI 计算器
 * <p>
 * 核心逻辑对齐东方财富 APP：
 * 1. 初始值 (第N天)：Average Gain/Loss = 前N天的 Sum(Gains/Losses) / N
 * 2. 递推值 (第N+1天及以后)：Average = (PrevAvg * (N-1) + CurrentGain/Loss) / N
 * 这种算法被称为 SMMA (Smoothed Moving Average)，确保了数据的平滑性和一致性。
 */
public class DtRSIIndicator extends CachedIndicator<Num> {
    // 修正指数，k < 1 会将 RSI 向 50 拉近
    private static final double RS_EXPONENT = 0.999;
    private final ClosePriceIndicator closePrice;
    private final int barCount; // 周期 N，例如 14
    private List<Num> rsiValues;

    public DtRSIIndicator(BarSeries series, int n) {
        super(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.barCount = n;
        preCalculate();
    }

    private void preCalculate() {
        int seriesLength = getBarSeries().getBarCount();
        rsiValues = new ArrayList<>(seriesLength);

        for (int i = 0; i < seriesLength; i++) {
            rsiValues.add(null);
        }

        if (seriesLength < barCount + 1) {
            return;
        }

        // 使用 BigDecimal 进行高精度中间计算
        BigDecimal avgGain = null;
        BigDecimal avgLoss = null;

        for (int i = 1; i < seriesLength; i++) { // 从 1 开始，因为需要前一天的数据
            Num currentClose = closePrice.getValue(i);
            Num prevClose = closePrice.getValue(i - 1);
            Num delta = currentClose.minus(prevClose);

            BigDecimal currentChange = delta.bigDecimalValue(); // 这是涨跌的绝对数值
            BigDecimal currentGain = currentChange.compareTo(BigDecimal.ZERO) > 0 ? currentChange : BigDecimal.ZERO;
            BigDecimal currentLoss = currentChange.compareTo(BigDecimal.ZERO) < 0 ? currentChange.abs() : BigDecimal.ZERO;

            if (i < barCount) {
                // 在达到 N+1 根K线之前，不计算RSI值
                continue;
            } else if (i == barCount) {
                // --- 初始化：计算前 N 天的平均涨幅和平均跌幅 ---
                BigDecimal sumGains = BigDecimal.ZERO;
                BigDecimal sumLosses = BigDecimal.ZERO;

                for (int j = 1; j <= barCount; j++) {
                    Num c = closePrice.getValue(j);
                    Num p = closePrice.getValue(j - 1);
                    Num d = c.minus(p);
                    BigDecimal bd = d.bigDecimalValue();

                    if (bd.compareTo(BigDecimal.ZERO) > 0) {
                        sumGains = sumGains.add(bd);
                    } else if (bd.compareTo(BigDecimal.ZERO) < 0) {
                        sumLosses = sumLosses.add(bd.abs());
                    }
                }

                // 计算平均值
                BigDecimal n = new BigDecimal(barCount);
                avgGain = sumGains.divide(n, 10, RoundingMode.HALF_UP);
                avgLoss = sumLosses.divide(n, 10, RoundingMode.HALF_UP);
            } else {
                // --- 递推（SMMA）：更新平均涨幅和平均跌幅 ---
                BigDecimal n = new BigDecimal(barCount);
                BigDecimal nMinusOne = n.subtract(BigDecimal.ONE);

                avgGain = avgGain.multiply(nMinusOne).add(currentGain).divide(n, 10, RoundingMode.HALF_UP);
                avgLoss = avgLoss.multiply(nMinusOne).add(currentLoss).divide(n, 10, RoundingMode.HALF_UP);
            }

            // --- 核心：严格按照东财公式 RSI = A / (A + B) * 100 计算 ---
            if (avgGain != null && avgLoss != null) {
                BigDecimal numerator = avgGain;
                BigDecimal denominator = avgGain.add(avgLoss);

                if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                    // 如果分母为0，说明当天价格没有变化，RSI为50
                    rsiValues.set(i, numOf(50.0));
                } else {
                    BigDecimal rsiBD = numerator.divide(denominator, 10, RoundingMode.HALF_UP).multiply(HUNDRED);
                    rsiValues.set(i, DecimalNum.valueOf(rsiBD));
                }
            }
        }
    }


    @Override
    protected Num calculate(int index) {
        return rsiValues.get(index);
    }


    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    /**
     * 获取整个序列的 RSI 列表 (用于调试或批量计算)
     */
    public List<Num> getAllValues() {
        return rsiValues;
    }

    /**
     * 判断 RSI 是否处于超买状态 (通常 > 70)
     */
    public boolean isOverbought(int index) {
        Num rsi = getValue(index);
        return rsi != null && rsi.isGreaterThan(NUM_70);
    }

    /**
     * 判断 RSI 是否处于超卖状态 (通常 < 30)
     */
    public boolean isOversold(int index) {
        Num rsi = getValue(index);
        return rsi != null && rsi.isLessThan(NUM_30);
    }

    public boolean isHighest(int index){
        return isHighestNum(rsiValues, getValue(index));
    }

    public boolean isLowest(int index){
        return isLowestNum(rsiValues, getValue(index));
    }

    /**
     * 判断 RSI 是否上穿特定值 (例如 30, 50, 70)
     */
    public boolean isCrossUp(int index, double level) {
        if (index <= 0) return false;
        Num prev = getValue(index - 1);
        Num curr = getValue(index);
        Num levelNum = numOf(level);
        if (prev == null || curr == null) return false;
        return prev.isLessThanOrEqual(levelNum) && curr.isGreaterThan(levelNum);
    }

    /**
     * 判断 RSI 是否下穿特定值 (例如 30, 50, 70)
     */
    public boolean isCrossDown(int index, double level) {
        if (index <= 0) return false;
        Num prev = getValue(index - 1);
        Num curr = getValue(index);
        Num levelNum = numOf(level);
        if (prev == null || curr == null) return false;
        return prev.isGreaterThanOrEqual(levelNum) && curr.isLessThan(levelNum);
    }
}