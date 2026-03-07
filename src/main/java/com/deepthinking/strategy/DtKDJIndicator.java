package com.deepthinking.strategy;

import lombok.Getter;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import static com.deepthinking.strategy.DtKDJIndicator.CrossStatus.*;
import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * KDJ  （东财 1min 5,2,2）
 */
public class DtKDJIndicator extends CachedIndicator<Num> {


    private final int N;
    private final int M1;
    private final int M2;

    private final ClosePriceIndicator close;
    private final HighestValueIndicator highestN;
    private final LowestValueIndicator lowestN;
    @Getter
    Num k;
    @Getter
    Num d;
    @Getter
    Num j;
    Num maxJ;
    Num minJ;

    public DtKDJIndicator(BarSeries series) {
        this(series, 5, 2, 2);
    }

    public DtKDJIndicator(BarSeries series, int n, int m1, int m2) {
        super(series);
        N = n;
        M1 = m1;
        M2 = m2;
        close = new ClosePriceIndicator(series);
        highestN = new HighestValueIndicator(new HighPriceIndicator(series), n);
        lowestN = new LowestValueIndicator(new LowPriceIndicator(series), n);
        int lastIdx = series.getEndIndex();
        k = calcK(lastIdx);
        d = calcD(lastIdx);
        j = k.multipliedBy(numOf(3)).minus(d.multipliedBy(numOf(2)));    //J = 3K - 2D
        maxJ = j;
        minJ = j;
        for (int i = lastIdx - N + 1; i < lastIdx; i++) {
            Num tj = calcJ(i);
            maxJ = maxJ.max(tj);
            minJ = minJ.min(tj);
        }
    }

    //RSV：最近5根1分钟K
    private Num calcRSV(int index) {
        Num h = highestN.getValue(index);
        Num l = lowestN.getValue(index);
        Num c = close.getValue(index);
        if (h.isEqual(l))
            return NUM_50;
        return c.minus(l).dividedBy(h.minus(l)).multipliedBy(NUM_100);
    }

    private Num calcK(int index) {
        if (index < N - 1)
            return NUM_50;                           // 第一个有效数据点，初始化 K=50, D=50
        Num rsv = calcRSV(index);
        if (index == N - 1)                         // 第一根有效K: ((M1-1)/M1 * 50) + ((1/M1) * RSV)
            return NUM_50.multipliedBy(numOf(M1 - 1)).dividedBy(numOf(M1)).plus(rsv.dividedBy(numOf(M1)));
        return calcK(index - 1).multipliedBy(numOf(M1 - 1)).dividedBy(numOf(M1))
                .plus(rsv.dividedBy(numOf(M1)));    // K = ((M1-1)/M1 * prevK)  + (1/M1) * RSV)
    }

    private Num calcD(int index) {
        if (index < N - 1) return NUM_50;
        Num k = calcK(index);
        if (index == N - 1)                         // 第一根有效D: ((M2-1)/M2 * 50) + ((1/M2) * K)
            return NUM_50.multipliedBy(numOf(M2 - 1)).dividedBy(numOf(M2)).plus(k.dividedBy(numOf(M2)));
        return calcD(index - 1).multipliedBy(numOf(M2 - 1)).dividedBy(numOf(M2))
                .plus(k.dividedBy(numOf(M2)));      // D = ((M2-1)/M2 * prevD) + ((1/M2) * K)
    }
    private Num calcJ(int index) {
        return calcK(index).multipliedBy(numOf(3)).minus(calcD(index).multipliedBy(numOf(2)));    //J = 3K - 2D
    }

    public enum CrossStatus {
        GOLDEN_CROSS,
        DEATH_CROSS,
        NONE
    }

    @Override
    protected Num calculate(int index) {
        return j;
    }

    public CrossStatus getCrossStatus(int index) {
        Num kPrev = calcK(index - 1);
        Num dPrev = calcD(index - 1);
        if (k.isGreaterThan(d) && kPrev.isLessThanOrEqual(dPrev)) {                     // 条件：今日 K > D 且 昨日 K <= D
            return GOLDEN_CROSS;
        } else if (k.isLessThan(d) && kPrev.isGreaterThanOrEqual(dPrev)) {
            return DEATH_CROSS;
        }
        return NONE;
    }


    public boolean isHighest() {
        return getJ().isGreaterThanOrEqual(maxJ);
    }

    public boolean isLowest() {
        return getJ().isLessThanOrEqual(minJ);
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}

