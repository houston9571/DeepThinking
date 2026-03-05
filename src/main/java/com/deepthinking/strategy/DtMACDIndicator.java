package com.deepthinking.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 东方财富版 MACD 指标（ta4j 0.22 + CachedIndicator）
 * 100%对齐东财分时/日线MACD数值，适配1分钟超短线场景
 */
public class DtMACDIndicator extends CachedIndicator<Num> {
    /**
     * 【最终修正版】严格对齐东方财富 APP 的 MACD
     *
     * 核心算法确认（基于国内软件通用标准）：
     * 1. EMA 初始值：EMA[0] = Close[0] (第一天的收盘价)
     * 2. 递推公式：EMA[i] = Close[i] * (2/(N+1)) + EMA[i-1] * (1 - 2/(N+1))
     * 3. DIF = EMA(12) - EMA(26)
     * 4. DEA = EMA(DIF, 9) [同样采用首日锚定]
     * 5. MACD 柱 = (DIF - DEA) * 2
     *
     * ⚠️ 重要前提：
     * 必须使用【前复权】收盘价数据！
     * 如果数据源是不复权的，结果将与东财 APP 完全不同。
     */

        private final ClosePriceIndicator closePrice;
        private final int fastPeriod;   // 12
        private final int slowPeriod;   // 26
        private final int signalPeriod; // 9

        private List<Num> difValues;
        private List<Num> deaValues;
        private List<Num> histogramValues;

        public DtMACDIndicator(BarSeries series) {
            this(series, 5, 13, 2);
        }

        public DtMACDIndicator(BarSeries series, int fast, int slow, int signal) {
            super(series);

            this.closePrice = new ClosePriceIndicator(series);
            this.fastPeriod = fast;
            this.slowPeriod = slow;
            this.signalPeriod = signal;

            preCalculate();
        }

        private void preCalculate() {
            int barCount = getBarSeries().getBarCount();
            difValues = new ArrayList<>(barCount);
            deaValues = new ArrayList<>(barCount);
            histogramValues = new ArrayList<>(barCount);

            // 1. 计算 EMA(12) 和 EMA(26) -> 采用首日锚定法
            Num[] emaFast = calculateEMASimpleAnchor(closePrice, fastPeriod, barCount);
            Num[] emaSlow = calculateEMASimpleAnchor(closePrice, slowPeriod, barCount);

            // 2. 计算 DIF
            List<Num> difList = new ArrayList<>(barCount);
            for (int i = 0; i < barCount; i++) {
                if (emaFast[i] != null && emaSlow[i] != null) {
                    difList.add(emaFast[i].minus(emaSlow[i]));
                } else {
                    difList.add(null);
                }
            }

            // 3. 计算 DEA = EMA(DIF, 9) -> 同样采用首日锚定法
            // 注意：DIF 序列前面可能有 null (因为 EMA26 还没算出来)，需要找到第一个非空值作为锚点
            Num[] deaArray = calculateEMASimpleAnchorFromList(difList, signalPeriod, barCount);

            // 4. 计算柱状图
            for (int i = 0; i < barCount; i++) {
                Num dif = difList.get(i);
                Num dea = deaArray[i];

                difValues.add(dif);
                deaValues.add(dea);

                if (dif != null && dea != null) {
                    // MACD 柱 = (DIF - DEA) * 2
                    Num bar = dif.minus(dea).multipliedBy(NUM_2);
                    histogramValues.add(bar);
                } else {
                    histogramValues.add(null);
                }
            }
        }

        /**
         * 【核心算法】首日锚定 EMA
         * 逻辑：
         * EMA[0] = Close[0]
         * EMA[i] = Close[i] * k + EMA[i-1] * (1-k)
         *
         * 这是国内软件在数据量充足时的实际表现。
         */
        private Num[] calculateEMASimpleAnchor(ClosePriceIndicator priceInd, int period, int count) {
            Num[] result = new Num[count];
            if (count == 0) return result;

            Num k = numOf(2).dividedBy(numOf(period + 1));
            Num oneMinusK = numOf(1).minus(k);

            // 锚定：EMA[0] = Close[0]
            Num currentEma = priceInd.getValue(0);
            result[0] = currentEma;

            // 递推
            for (int i = 1; i < count; i++) {
                Num price = priceInd.getValue(i);
                currentEma = price.multipliedBy(k).plus(currentEma.multipliedBy(oneMinusK));
                result[i] = currentEma;
            }

            return result;
        }

        /**
         * 【核心算法】List 序列的首日锚定 EMA (用于计算 DEA)
         */
        private Num[] calculateEMASimpleAnchorFromList(List<Num> data, int period, int count) {
            Num[] result = new Num[count];
            if (count == 0 || data.isEmpty()) return result;

            // 找到第一个非空值作为锚点
            int anchorIndex = -1;
            for (int i = 0; i < count; i++) {
                if (data.get(i) != null) {
                    anchorIndex = i;
                    break;
                }
            }

            if (anchorIndex == -1) return result;

            Num numPrototype = data.get(anchorIndex);
            Num k = numOf(2).dividedBy(numOf(period + 1));
            Num oneMinusK = numOf(1).minus(k);

            // 锚定：EMA[anchorIndex] = Data[anchorIndex]
            Num currentEma = data.get(anchorIndex);
            result[anchorIndex] = currentEma;

            // 递推
            for (int i = anchorIndex + 1; i < count; i++) {
                Num val = data.get(i);
                if (val == null) {
                    // 理论上 DIF 一旦开始就不会中断，除非数据源缺失
                    result[i] = null;
                    continue;
                }
                currentEma = val.multipliedBy(k).plus(currentEma.multipliedBy(oneMinusK));
                result[i] = currentEma;
            }

            return result;
        }

        @Override
        protected Num calculate(int index) {
            return histogramValues.get(index);
        }

        public Num getDIF(int index) {
            return difValues.get(index);
        }

        public Num getDEA(int index) {
            return deaValues.get(index);
        }

        public Num getHistogram(int index) {
            return histogramValues.get(index);
        }

        public boolean isGoldenCross(int index) {
            if (index <= 0) return false;
            Num prevDif = getDIF(index - 1);
            Num prevDea = getDEA(index - 1);
            Num currDif = getDIF(index);
            Num currDea = getDEA(index);
            if (prevDif == null || prevDea == null || currDif == null || currDea == null) return false;
            return prevDif.isLessThan(prevDea) && currDif.isGreaterThan(currDea);
        }

        public boolean isDeathCross(int index) {
            if (index <= 0) return false;
            Num prevDif = getDIF(index - 1);
            Num prevDea = getDEA(index - 1);
            Num currDif = getDIF(index);
            Num currDea = getDEA(index);
            if (prevDif == null || prevDea == null || currDif == null || currDea == null) return false;
            return prevDif.isGreaterThan(prevDea) && currDif.isLessThan(currDea);
        }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}