package com.deepthinking.strategy;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.strategy.DtOBVMAIndicator.CrossStatus.*;
import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 【东财官方公式版】OBVMA (能量潮移动平均线) 计算器
 *
 * 核心算法：
 * 1. 计算标准 OBV 指标。
 *    - OBV[0] = Volume[0]
 *    - If Close[i] > Close[i-1], then OBV[i] = OBV[i-1] + Volume[i]
 *    - If Close[i] < Close[i-1], then OBV[i] = OBV[i-1] - Volume[i]
 *    - If Close[i] == Close[i-1], then OBV[i] = OBV[i-1]
 * 2. 对 OBV 序列计算 N 日简单移动平均 (SMA)，得到 OBVMA。
 *    - 东财默认参数为 N=30。
 */
public class DtOBVMAIndicator extends CachedIndicator<Num> {

    private final ClosePriceIndicator closePrice;
    private final VolumeIndicator volume;
    private final int obvmaBarCount; // OBVMA 周期 N (例如 30)
    private final SMAIndicator smaOfObv; // 对 OBV 序列求 SMA
    private List<Num> obvValues; // 存储计算好的 OBV 值

    public DtOBVMAIndicator(BarSeries series, int n) {
        super(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.volume = new VolumeIndicator(series);
        this.obvmaBarCount = n;

        // 第一步：预计算完整的 OBV 序列
        preCalculateObv();

        // 第二步：基于计算好的 OBV 序列，创建一个 SMA 指标
        // 这里的 Num[] 需要从 List<Num> 转换而来
        // 为了简化，我们直接在 calculate 方法中调用预计算好的 OBV 值
        this.smaOfObv = new SMAIndicator(new PreCalculatedObvIndicator(series, obvValues), n);
    }
    /**
     * 预计算整个时间序列的 OBV 值，严格按照东财公式。
     */
    private void preCalculateObv() {
        int seriesLength = getBarSeries().getBarCount();
        obvValues = new ArrayList<>(seriesLength);

        if (seriesLength == 0) {
            return;
        }

        // 根据东财公式，VA 的累积从第一天开始
        // 对于第一天，没有 REF(CLOSE,1)，通常 VA[0] = 0 或者 VA[0] = VOL[0]
        // 根据 SUM 的逻辑和常见实践，第一天的 VA 通常为 0，因为没有比较基准。
        // 但为了与东财对齐，我们尝试两种情况。搜索结果和实践表明，第一天通常为 0。
        obvValues.add(NUM_0);

        for (int i = 1; i < seriesLength; i++) {
            Num currentClose = closePrice.getValue(i);
            Num prevClose = closePrice.getValue(i - 1);
            Num currentVolume = volume.getValue(i);
            Num previousObv = obvValues.get(i - 1);
            // --- 核心：计算 VA (Volume Action) ---
            Num va;
            if (currentClose.isGreaterThan(prevClose)) {
                // 价格上涨，VA = +VOL
                va = currentVolume;
            } else {
                // 价格下跌或持平，VA = -VOL
                va = currentVolume.negate();
            }

            // --- 核心：OBV 是 VA 的累积和 ---
            Num currentObv = previousObv.plus(va);
            obvValues.add(currentObv);
        }
    }

    public Num getObv(int index){
        return obvValues.get(index);
    }

    public Num getObvMa(int index) {
        return smaOfObv.getValue(index);
    }

    public boolean isHighest(int index){
        return isHighestNum(obvValues, getObv(index));
    }

    public boolean isObvUTurnDown(int index){
       return getObv(index).isLessThan(getObv(index - 1));
    }

    public boolean isLowest(int index){
        return isLowestNum(obvValues, getObv(index));
    }

    public boolean isObvUTurnUp(int index){
        return getObv(index).isGreaterThan(getObv(index - 1));
    }

    public enum CrossStatus {
        GOLDEN_CROSS,
        DEATH_CROSS,
        NONE
    }

    public CrossStatus getCrossStatus(int index){
        if(obvValues.get(index).isGreaterThan(smaOfObv.getValue(index)) && obvValues.get(index-1).isLessThanOrEqual(smaOfObv.getValue(index-1)) ){
            return GOLDEN_CROSS;
        }else if (obvValues.get(index).isLessThan(smaOfObv.getValue(index)) && obvValues.get(index-1).isGreaterThanOrEqual(smaOfObv.getValue(index-1)) ){
            return DEATH_CROSS;
        }
        return NONE;
    }

    @Override
    protected Num calculate(int index) {
        return obvValues.get(index);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }

    /**
     * 一个辅助类，用于包装预计算好的 OBV 数组，使其成为一个 Ta4j Indicator。
     * 这样就可以直接传给 SMAIndicator 构造函数。
     */
    private static class PreCalculatedObvIndicator extends CachedIndicator<Num> {
        private final List<Num> values;

        public PreCalculatedObvIndicator(BarSeries series, List<Num> calculatedValues) {
            super(series);
            this.values = calculatedValues;
        }

        @Override
        protected Num calculate(int index) {
            return values.get(index);
        }

        @Override
        public int getCountOfUnstableBars() {
            return 0;
        }
    }
}