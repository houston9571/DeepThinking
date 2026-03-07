package com.deepthinking.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 东方财富版 MACD 指标（ta4j 0.22 + CachedIndicator）
 * 100%对齐东财分时/日线MACD数值，适配1分钟超短线场景
 */
public class DtMACDIndicator extends CachedIndicator<Num> {

    private List<Num> difValues;
    private List<Num> deaValues;
    private List<Num> histogramValues;

    public DtMACDIndicator(BarSeries series, int fast, int slow, int signal) {
        super(series);

        // 1. 计算快慢 EMA (严格东财逻辑)
        DtEMAIndicator emaFast = new DtEMAIndicator(series, fast);
        DtEMAIndicator emaSlow = new DtEMAIndicator(series, slow);

        // 2. 构建 DIF 序列 (作为临时 TimeSeries 或直接列表处理)
        // 这里为了复用 EastMoneyEMA，我们需要先生成 DIF 列表，再包装成一个临时的 Indicator
        int count = series.getBarCount();
        List<Num> difList = new ArrayList<>(count);
        difValues = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Num fastVal = emaFast.getValue(i);
            Num slowVal = emaSlow.getValue(i);
            if (fastVal != null && slowVal != null) {
                Num dif = fastVal.minus(slowVal);
                difList.add(dif);
                difValues.add(dif);
            } else {
                difList.add(null);
                difValues.add(null);
            }
        }

        // 3. 计算 DEA = EMA(DIF, signal)
        // 注意：DIF 前面可能有 null (因为 SlowEMA 需要时间预热)，但 EastMoneyEMA 逻辑是 Close[0]=EMA[0]
        // 对于 DIF 序列，我们需要找到第一个非空值作为 "Close[0]" 的等价物
        deaValues = calculateEMAFromList(difList, signal);

        // 4. 计算柱状图
        histogramValues = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Num dif = difValues.get(i);
            Num dea = deaValues.get(i);
            if (dif != null && dea != null) {
                histogramValues.add(dif.minus(dea).multipliedBy(NUM_2));
            } else {
                histogramValues.add(null);
            }
        }
    }

    // ... (getter 方法同上)

    @Override
    protected Num calculate(int index) {
        return histogramValues.get(index);
    }

    public Num getHistogram(int index) {
        return histogramValues.get(index);
    }

    public Num getDIF(int index) {
        return difValues.get(index);
    }

    public Num getDEA(int index) {
        return deaValues.get(index);
    }


    public boolean isHighest(int index){
        return isHighestNum(difValues, getDIF(index));
    }

    public boolean isLowest(int index){
        return isLowestNum(difValues, getDIF(index));
    }

    public enum CrossStatus {
        GOLDEN_CROSS_RED,
        GOLDEN_CROSS,
        DEATH_CROSS,
        DEATH_CROSS_GREEN,
        NONE
    }


    public CrossStatus getCrossStatus(int index){
        if(getDIF(index).isGreaterThan(getDEA(index)) && getDIF(index-1).isLessThanOrEqual(getDEA(index-1)) ){
            if(isRedExpand(index))
                return CrossStatus.GOLDEN_CROSS_RED;
            return CrossStatus.GOLDEN_CROSS;
        }else if (getDIF(index).isLessThan(getDEA(index)) && getDIF(index-1).isGreaterThanOrEqual(getDEA(index-1)) ){
            if(isGreenExpand(index))
                return CrossStatus.DEATH_CROSS_GREEN;
            return CrossStatus.DEATH_CROSS;
        }
        return CrossStatus.NONE;
    }

    // 红柱放大
    public boolean isRedExpand(int index){
        return getHistogram(index).isPositive() && getHistogram(index).isGreaterThan(getHistogram(index-1));
    }

    // 绿柱放大
    public boolean isGreenExpand(int index){
        return getHistogram(index).isNegative() && getHistogram(index).isLessThan(getHistogram(index-1));
    }


    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }


    public List<Num> calculateEMAFromList(List<Num> inputData, int period) {
        List<Num> result = new ArrayList<>(inputData.size());
        Num alpha = NUM_2.dividedBy(numOf(period + 1));
        Num oneMinusAlpha = NUM_1.minus(alpha);

        // 寻找第一个非空值作为种子
        int seedIndex = -1;
        for (int i = 0; i < inputData.size(); i++) {
            if (inputData.get(i) != null) {
                seedIndex = i;
                break;
            }
        }

//        if (seedIndex == -1) return;

        // 种子：EMA[seedIndex] = Data[seedIndex]
        Num currentEma = inputData.get(seedIndex);

        // 填充前面的 null
        for (int i = 0; i < seedIndex; i++) {
            result.add(null);
        }

        result.add(currentEma);

        // 递推
        for (int i = seedIndex + 1; i < inputData.size(); i++) {
            Num val = inputData.get(i);
            if (val == null) {
                result.add(null);
                continue;
            }
            currentEma = val.multipliedBy(alpha).plus(currentEma.multipliedBy(oneMinusAlpha));
            result.add(currentEma);
        }
        return result;
    }

}