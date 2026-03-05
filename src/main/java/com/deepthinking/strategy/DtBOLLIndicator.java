package com.deepthinking.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.strategy.StrategyUtils.*;

/**
 * 【终极修复版】严格对齐东方财富 APP 的 BOLL 指标
 * <p>
 * 核心算法确认：
 * 1. 中轨 (MB) = SMA(Close, N)
 * 2. 标准差 (MD) = sqrt( sum((Close - MB)^2) / N )  <-- 关键：分母是 N，不是 N-1
 * 3. 上轨 (UP) = MB + 2 * MD
 * 4. 下轨 (DN) = MB - 2 * MD
 * <p>
 * ⚠️ 重要前提：
 * 必须使用【前复权】收盘价数据！
 */
public class DtBOLLIndicator extends CachedIndicator<Num> {

    private final ClosePriceIndicator closePrice;
    private final int barCount;     // 周期 N，默认 20
    private final double k;         // 倍数，默认 2.0

    private List<Num> mbValues; // 中轨
    private List<Num> upValues; // 上轨
    private List<Num> dnValues; // 下轨
    private List<Num> mdValues; // 标准差

    public DtBOLLIndicator(BarSeries series) {
        this(series, 10, 2.0);
    }

    public DtBOLLIndicator(BarSeries series, int n, double k) {
        super(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.barCount = n;
        this.k = k;
        preCalculate();
    }

    private void preCalculate() {
        int seriesLength = getBarSeries().getBarCount();
        mbValues = new ArrayList<>(seriesLength);
        upValues = new ArrayList<>(seriesLength);
        dnValues = new ArrayList<>(seriesLength);
        mdValues = new ArrayList<>(seriesLength);

        for (int i = 0; i < seriesLength; i++) {
            // 如果数据不足 N 根，返回 null (与东财一致，不显示)
            if (i < barCount - 1) {
                mbValues.add(null);
                upValues.add(null);
                dnValues.add(null);
                mdValues.add(null);
                continue;
            }

            // 1. 计算中轨 MB (SMA)
            // MB = Sum(Close, N) / N
            Num sum = NUM_0;
            for (int j = 0; j < barCount; j++) {
                sum = sum.plus(closePrice.getValue(i - j));
            }
            Num mb = sum.dividedBy(numOf(barCount));

            // 2. 计算标准差 MD
            // MD = sqrt( Sum((Close - MB)^2) / N )  <-- 重点：除以 N
            Num varianceSum = NUM_0;
            for (int j = 0; j < barCount; j++) {
                Num diff = closePrice.getValue(i - j).minus(mb);
                varianceSum = varianceSum.plus(diff.multipliedBy(diff));
            }

            // 关键步骤：除以 N (总体标准差)
            Num variance = varianceSum.dividedBy(numOf(barCount));
            Num md = sqrt(variance);

            // 3. 计算上下轨
            Num kNum = numOf(k);
            Num up = mb.plus(md.multipliedBy(kNum));
            Num dn = mb.minus(md.multipliedBy(kNum));

            mbValues.add(mb);
            mdValues.add(md);
            upValues.add(up);
            dnValues.add(dn);
        }
    }

    /**
     * 手动实现平方根计算 (Newton-Raphson 方法)
     * 因为 ta4j 的 Num 接口不一定直接提供 sqrt，且为了保持精度统一
     */
    private Num sqrt(Num value) {
        if (value.isZero()) return value;
        if (value.isLessThan(NUM_0)) {
            throw new ArithmeticException("Cannot calculate square root of negative number");
        }

        // 初始猜测
        Num x = value;
        Num lastX = NUM_0;

        // 牛顿迭代法，直到收敛
        // 精度设置为 1e-10 (根据 Num 的实现，可能需要调整迭代次数)
        for (int i = 0; i < 100; i++) {
            lastX = x;
            // x_new = 0.5 * (x + value / x)
            x = x.plus(value.dividedBy(x)).multipliedBy(numOf(0.5));

            // 检查收敛 (差值极小)
            if (x.minus(lastX).abs().isLessThan(numOf(1e-10))) {
                break;
            }
        }
        return x;
    }

    @Override
    protected Num calculate(int index) {
        return mbValues.get(index);
    }

    /**
     * 获取中轨 (MB)
     */
    public Num getMid(int index) {
        return mbValues.get(index);
    }

    /**
     * 获取上轨 (UP)
     */
    public Num getUpper(int index) {
        return upValues.get(index);
    }

    /**
     * 获取下轨 (DN)
     */
    public Num getLower(int index) {
        return dnValues.get(index);
    }

    /**
     * 获取标准差 (MD)
     */
    public Num getMD(int index) {
        return mdValues.get(index);
    }

    /**
     * 判断是否突破上轨
     */
    public boolean isBreakoutUp(int index) {
        Num close = closePrice.getValue(index);
        Num up = getUpper(index);
        if (close == null || up == null) return false;
        return close.isGreaterThan(up);
    }

    /**
     * 判断是否跌破下轨
     */
    public boolean isBreakoutDown(int index) {
        Num close = closePrice.getValue(index);
        Num dn = getLower(index);
        if (close == null || dn == null) return false;
        return close.isLessThan(dn);
    }

    /**
     * 获取带宽 (UP - DN)
     */
    public Num getWidth(int index) {
        Num up = getUpper(index);
        Num dn = getLower(index);
        if (up == null || dn == null) return null;
        return up.minus(dn);
    }

    // ================= 新增功能：开口方向判断 =================

    /**
     * 布林带开口状态枚举
     */
    public enum MouthStatus {
        OPENING,    // 开口 (带宽变大，波动加剧)
        SQUEEZING,  // 收口 (带宽变小，震荡整理)
        PARALLEL,   // 平行 (带宽基本不变)
        UNKNOWN     // 数据不足
    }

    /**
     * 获取当前 K 线的开口状态
     * 逻辑：比较 当前带宽 与 前一根 K 线 的带宽
     */
    public MouthStatus getMouthStatus(int index) {
        if (index <= 0 || getWidth(index) == null || getWidth(index - 1) == null) {
            return MouthStatus.UNKNOWN;
        }

        Num currentWidth = getWidth(index);
        Num prevWidth = getWidth(index - 1);

        // 计算变化率: (Current - Prev) / Prev
        // 防止除以零
        if (prevWidth.isZero()) {
            return MouthStatus.UNKNOWN;
        }

        Num changeRatio = currentWidth.minus(prevWidth).dividedBy(prevWidth);

        // 阈值设定：变化超过 1% 视为有效变化，否则视为平行
        // 可以根据实际需求调整这个阈值 (例如 0.005 即 0.5%)
        Num threshold = numOf(0.01);
        if (changeRatio.isGreaterThan(threshold)) {
            return MouthStatus.OPENING;
        } else if (changeRatio.isLessThan(threshold.negate())) {
            return MouthStatus.SQUEEZING;
        } else {
            return MouthStatus.PARALLEL;
        }
    }

    // ================= 新增功能：中轨倾斜度 =================

    /**
     * 中轨趋势状态枚举
     */
    public enum MidrailTrend {
        STRONG_UP,      // 陡峭上涨    趋势：强劲上涨 (中轨陡峭向上)
        WEAK_UP,        // 缓步上涨    趋势：温和上涨
        FLAT,           // 走平
        WEAK_DOWN,      // 缓步下跌    趋势：横盘震荡 (中轨走平)
        STRONG_DOWN,    // 陡峭下跌    趋势：强劲下跌 (中轨陡峭向下)
        UNKNOWN
    }


    // ================= 辅助方法 =================

    /**
     * 综合策略信号示例：
     * "开口上涨"：中轨向上倾斜 + 布林带开口
     */
    public boolean isBullishBreakout(int index) {
        MouthStatus mouth = getMouthStatus(index);
        MidrailTrend trend = getMidrailTrend(index);
        return (mouth == MouthStatus.OPENING || mouth == MouthStatus.PARALLEL) &&
                (trend == MidrailTrend.STRONG_UP || trend == MidrailTrend.WEAK_UP);
    }

    /**
     * 综合策略信号示例：
     * "收口盘整"：布林带收口 + 中轨走平或下倾
     */
    public boolean isConsolidation(int index) {
        MouthStatus mouth = getMouthStatus(index);
        MidrailTrend trend = getMidrailTrend(index);
        return mouth == MouthStatus.SQUEEZING && (trend == MidrailTrend.FLAT || trend == MidrailTrend.WEAK_DOWN || trend == MidrailTrend.STRONG_DOWN);
    }

    /**
     * 获取中轨的斜率数值
     * 公式：Slope = (MB[index] - MB[index - lookBack]) / lookBack
     * lookBack 默认为 5，表示看过去 5 根 K 线的平均斜率，以平滑噪音
     * 基于斜率百分比进行判断
     */
    public MidrailTrend getMidrailTrend(int index) {
        int lookBack = 5;
        if (index < lookBack || getMid(index) == null || getMid(index - lookBack) == null) {
            return null;
        }
        Num currentMB = getMid(index);
        Num pastMB = getMid(index - lookBack);
        // 斜率 = (Y2 - Y1) / X2 - X1 (这里 X 间隔就是 lookBack)
        Num slope = currentMB.minus(pastMB).dividedBy(numOf(lookBack));
        if (slope == null || currentMB.isZero()) {
            return null;
        }
        // 返回百分比数值 (例如 0.05 代表 5%)
        Num slopePercent = slope.dividedBy(currentMB).multipliedBy(NUM_100);
        if (slopePercent == null) return MidrailTrend.UNKNOWN;

        double val = slopePercent.doubleValue();
        // 阈值可根据品种波动性调整，日线级别 0.5% ~ 1% 作为分界
        if (val > 1.0) return MidrailTrend.STRONG_UP;
        if (val > 0.1) return MidrailTrend.WEAK_UP;
        if (val < -1.0) return MidrailTrend.STRONG_DOWN;
        if (val < -0.1) return MidrailTrend.WEAK_DOWN;

        return MidrailTrend.FLAT;
    }

    @Override
    public int getCountOfUnstableBars() {
        return 0;
    }
}