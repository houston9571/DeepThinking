package com.deepthinking.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * KDJ  （东财 1min 5,2,2）
 */
public class DtKDJIndicator extends CachedIndicator<Num[]> {


    private final int N;
    private final int M1;
    private final int M2;

    private final ClosePriceIndicator close;
    private final HighestValueIndicator highestN;
    private final LowestValueIndicator lowestN;

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
    }

    //RSV：最近5根1分钟K
    private Num getRSV(int index) {
        Num h = highestN.getValue(index);
        Num l = lowestN.getValue(index);
        Num c = close.getValue(index);
        if (h.isEqual(l))
            return NUM_50;
        return c.minus(l).dividedBy(h.minus(l)).multipliedBy(NUM_100);
    }

    private Num getK(int index) {
        if (index < N - 1)
            return NUM_50;                           // 第一个有效数据点，初始化 K=50, D=50
        Num rsv = getRSV(index);
        if (index == N - 1)                         // 第一根有效K: ((M1-1)/M1 * 50) + ((1/M1) * RSV)
            return NUM_50.multipliedBy(numOf(M1 - 1)).dividedBy(numOf(M1)).plus(rsv.dividedBy(numOf(M1)));
        return getK(index - 1).multipliedBy(numOf(M1 - 1)).dividedBy(numOf(M1))
                .plus(rsv.dividedBy(numOf(M1)));    // K = ((M1-1)/M1 * prevK)  + (1/M1) * RSV)
    }

    private Num getD(int index) {
        if (index < N - 1) return NUM_50;
        Num k = getK(index);
        if (index == N - 1)                         // 第一根有效D: ((M2-1)/M2 * 50) + ((1/M2) * K)
            return NUM_50.multipliedBy(numOf(M2 - 1)).dividedBy(numOf(M2)).plus(k.dividedBy(numOf(M2)));
        return getD(index - 1).multipliedBy(numOf(M2 - 1)).dividedBy(numOf(M2))
                .plus(k.dividedBy(numOf(M2)));      // D = ((M2-1)/M2 * prevD) + ((1/M2) * K)
    }

    private Num getJ(int index) {
        return getK(index).multipliedBy(numOf(3)).minus(getD(index).multipliedBy(numOf(2)));    //J = 3K - 2D
    }

    @Override
    protected Num[] calculate(int index) {
        Num k = getK(index);
        Num d = getD(index);
        Num j = k.multipliedBy(numOf(3)).minus(d.multipliedBy(numOf(2)));    //J = 3K - 2D
        Num kPrev = getK(index - 1);
        Num dPrev = getD(index - 1);
        Num cross = NONE;
        if (k.isGreaterThan(d) && kPrev.isLessThanOrEqual(dPrev)) {                     // 条件：今日 K > D 且 昨日 K <= D
            cross = GOLDEN_CROSS;
        } else if (k.isLessThan(d) && kPrev.isGreaterThanOrEqual(dPrev)) {
            cross = DEATH_CROSS;
        }
        Num maxJ = j;
        Num minJ = j;
        for (int i = index - N + 1; i < index; i++) {
            Num tj = getJ(i);
            maxJ = maxJ.max(tj);
            minJ = minJ.min(tj);
        }
        return new Num[]{k, d, j, maxJ, minJ, cross};
    }

    @Override
    public int getCountOfUnstableBars() {
        return N;
    }
}
