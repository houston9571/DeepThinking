package com.deepthinking.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.StringUtil;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;
import com.deepthinking.strategy.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.constant.Constants.*;
import static com.deepthinking.strategy.DtKDJIndicator.CrossStatus.DEATH_CROSS;
import static com.deepthinking.strategy.DtKDJIndicator.CrossStatus.GOLDEN_CROSS;
import static com.deepthinking.strategy.StrategyUtils.*;
import static java.math.BigDecimal.ZERO;

@Slf4j
@Service
@RequiredArgsConstructor
public class Ta4jMinuteIndicatorCalculator {
    // ===================== 超短线参数配置（核心） =====================
    // EMA参数
    private static final int EMA5_PERIOD = 5;
    private static final int EMA10_PERIOD = 10;
    // MACD参数
    private static final int MACD_FAST = 12;
    private static final int MACD_SLOW = 26;
    private static final int MACD_SIGNAL = 9;
    // RSI参数
    private static final int RSI6_PERIOD = 6;
    // KDJ参数（分时最优：5,2,2）
    private static final int KDJ_N = 5;
    private static final int KDJ_M1 = 2;
    private static final int KDJ_M2 = 2;
    // WR参数
    private static final int WR6_PERIOD = 6;
    // BOLL参数（分时：20周期，1.5/2.0标准差）
    private static final int BOLL_PERIOD = 20;
    private static final double BOLL_DEV_1MIN = 1.5;
    private static final double BOLL_DEV_5MIN = 2.0;
    // VMACD参数（量能MACD）
    private static final int VMACD_FAST = 12;
    private static final int VMACD_SLOW = 26;
    private static final int VMACD_SIGNAL = 9;
    // OBV+OBVM5参数
    private static final int OBV_MA5_PERIOD = 5;

    /**
     * ================= 实时计算分时指标 ====================
     * 日线共振：确定股票能不能做
     * 分时指标：确定什么时候买
     * 双重共振：胜率可达 70%~85%（超短线 1-3 天）
     * 所有指标周期统一，无滞后、无冲突
     */
    public static StockTechMinute calcMinuteIndicator(List<StockTechMinute> list) {
        int size = list.size();
        StockTechMinute tech = list.getLast();
        // 至少需要10分钟数据（适配分时MA10/BOLL10）
        if (list.size() < 5) {
            log.warn("分时数据不足不计算，必须满足5条");
            return tech;
        }

        BaseBarSeries series = new BaseBarSeriesBuilder().withName(tech.getStockCode() + tech.getStockName()).build();
        for (StockTechMinute t : list) {
            Instant tr = t.getTradeDate().atTime(t.getTradeTime()).atZone(ZoneId.of(ZONE_ID)).toInstant();
            series.addBar(new BaseBar(Duration.ofMinutes(1), tr.minusSeconds(60), tr,
                    DecimalNum.valueOf(t.getOpen()),
                    DecimalNum.valueOf(t.getHigh()),
                    DecimalNum.valueOf(t.getLow()),
                    DecimalNum.valueOf(t.getClose()),
                    DecimalNum.valueOf(t.getVolume()),
                    DecimalNum.valueOf(t.getAmount()),
                    1));
        }

        BigDecimal currClose = tech.getClose();
        int lastIndex = series.getEndIndex();
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        VolumeIndicator volumeIndicator = new VolumeIndicator(series);
        double ma5 = new SMAIndicator(volumeIndicator, 5).getValue(lastIndex).doubleValue();
        tech.setVolumeRatio(BigDecimal.valueOf(tech.getVolume() / ma5));

        // 1. EMA（指数移动平均） 短线参数：3 5 10   确定当前波段的多空基调     -- 隔夜条件：价格站上 EMA5/EMA10 → 隔夜安全；跌破 EMA10 → 不隔夜。
        DtEMAIndicator ema3 = new DtEMAIndicator(closePriceIndicator, 3);
        DtEMAIndicator ema5 = new DtEMAIndicator(closePriceIndicator, 5);
        DtEMAIndicator ema10 = new DtEMAIndicator(closePriceIndicator, 10);
        Num ema5Num = ema5.getValue(lastIndex);
        Num ema10Num = ema10.getValue(lastIndex);
        tech.setEma3(ema3.getValue(lastIndex).bigDecimalValue());
        tech.setEma5(ema5Num.bigDecimalValue());
        tech.setEma10(ema10Num.bigDecimalValue());

        DtBIASIndicator biasInd = new DtBIASIndicator(series, 5);
        tech.setBias(biasInd.getValue(lastIndex).bigDecimalValue());

        // 2. MACD（平滑异同移动平均指数）（趋势+动能） 短线参数(fast=5, slow=13, signal=2)   零轴确定长短周期动量方向    -- 隔夜条件：MACD红柱、DIF > DEA。
        DtMACDIndicator macdInd = new DtMACDIndicator(closePriceIndicator, 5, 13, 2);    // 柱状图 (Histogram) = MACD线 - 信号线
        DtMACDIndicator.CrossStatus macdStatus = macdInd.getCrossStatus(lastIndex);
        tech.setMacdDif(macdInd.getDIF(lastIndex).bigDecimalValue());
        tech.setMacdDea(macdInd.getDEA(lastIndex).bigDecimalValue());
        tech.setMacdBar(macdInd.getHistogram(lastIndex).bigDecimalValue());
        tech.setMacdStatus(macdStatus);

        // 3. BOLL（布林带）短线参数：10 2  衡量价格相对于波动的边界位置   -- 隔夜条件：价格在中轨之上，可持仓过夜，若跌破中轨则需离场
        DtBOLLIndicator bollInd = new DtBOLLIndicator(series, 10, 2);
        tech.setBollMid(bollInd.getMid(lastIndex).bigDecimalValue());
        tech.setBollUpper(bollInd.getUpper(lastIndex).bigDecimalValue());
        tech.setBollLower(bollInd.getLower(lastIndex).bigDecimalValue());
        tech.setBollMouthStatus(bollInd.getMouthStatus(lastIndex));
        tech.setBollMidTrend(bollInd.getMidTrend(lastIndex));

        // 4. RSI（相对强弱指标） 超短线最灵：6    衡量市场强弱与超买超卖
        DtRSIIndicator rsiInd = new DtRSIIndicator(series, 6);
        tech.setRsi6(rsiInd.getValue(lastIndex).bigDecimalValue());

        // 5. KDJ（随机指标）短线参数：5 2 2   对短线拐点极其灵敏    -- 隔夜条件：J 在 50~80 之间最稳；J>90 不隔夜。   默认算法可能与通达信/同花顺略有差异（平滑方式）
        DtKDJIndicator kdjInd = new DtKDJIndicator(series, 5, 2, 2);
        Num k = kdjInd.getK();
        Num d = kdjInd.getD();
        Num j = kdjInd.getJ();
        tech.setKdjK(k.bigDecimalValue());
        tech.setKdjD(d.bigDecimalValue());
        tech.setKdjJ(j.bigDecimalValue());
        tech.setKdjStatus(kdjInd.getCrossStatus(lastIndex));


        // 6. WR（威廉指标）极短线参数：6   用于1分钟或5分钟线，适合捕捉极速脉冲行情，预判趋势衰减      -- 隔夜条件：WR < 20 超买 → 不隔夜; WR > 80 超卖 → 可低吸隔夜; WR从超卖区回升时配合OBV放量可加仓。
        DtWRIndicator wrInd = new DtWRIndicator(series, 6);
        Num wr = wrInd.getValue(lastIndex);
        tech.setWr6(wr.bigDecimalValue());

        // 7. VMACD（成交量MACD）  短线参数：5,13,1   量平滑异同平均，量化资金动能    -- 隔夜条件：VMACD 红柱 → 量价配合
        DtVMACDIndicator vmacdInd = new DtVMACDIndicator(volumeIndicator, 5, 13, 2);
        DtVMACDIndicator.CrossStatus vmacdStatus = vmacdInd.getCrossStatus(lastIndex);
        tech.setVmacdDif(vmacdInd.getDIF(lastIndex).bigDecimalValue());
        tech.setVmacdDea(vmacdInd.getDEA(lastIndex).bigDecimalValue());
        tech.setVmacdBar(vmacdInd.getHistogram(lastIndex).bigDecimalValue());
        tech.setVmacdStatus(vmacdStatus);

        // 8. OBV_MA 能量潮均线确认资金流入流出     -- 隔夜条件：OBV > OBV_MA5
        DtOBVMAIndicator obvmaInd = new DtOBVMAIndicator(series, 5);
        Num obvNum = obvmaInd.getObv(lastIndex);
        Num obvMa5Num = obvmaInd.getObvMa(lastIndex);
        tech.setObv(obvNum.longValue());
        tech.setObvMa5(obvMa5Num.longValue());
        tech.setObvStatus(obvmaInd.getCrossStatus(lastIndex));
//        log.info("-----计算分时指标：{}", JSONObject.toJSONString(tech));

        // ----------- 顶底背离 ---------------
        Num highestN = new HighestValueIndicator(new HighPriceIndicator(series), size / 2).getValue(lastIndex);
        Num lowestN = new LowestValueIndicator(new LowPriceIndicator(series), size / 2).getValue(lastIndex);
        tech.setDivergenceType(DivergenceType.NONE);
        short divergenceStrength = 0;
        // 背离（Divergence）是指价格走势与动量指标（如 MACD、RSI、KDJ、OBV）的趋势方向相反，暗示当前动能正在放缓。
        if (currClose.compareTo(highestN.bigDecimalValue()) == 0 && !macdInd.isHighest(lastIndex)) {
            tech.setDivergenceType(DivergenceType.TOP);
            divergenceStrength++;           // 基础分：VMACD顶背离
            if (!vmacdInd.isHighest(lastIndex)) {
                divergenceStrength++;       // VMACD红柱缩小
            }
            if (!kdjInd.isHighest()) {       // KDJ-J未新高 → +1分
                divergenceStrength++;
            }
            if (!rsiInd.isHighest(lastIndex) || rsiInd.isOversold(lastIndex)) {
                divergenceStrength++;       // RSI顶背离或超买
            }
            if (!obvmaInd.isHighest(lastIndex) || obvmaInd.isObvUTurnDown(lastIndex)) {
                divergenceStrength++;       // OBV顶背离 OBV能量潮掉头
            }

        } else if (currClose.compareTo(lowestN.bigDecimalValue()) == 0 && !macdInd.isLowest(lastIndex)) {
            tech.setDivergenceType(DivergenceType.BOTTOM);
            divergenceStrength++;
            if (vmacdInd.isLowest(lastIndex)) {
                divergenceStrength++;       // VMACD绿柱缩小
            }
            if (!kdjInd.isLowest()) {       // KDJ-J未新低 → +1分
                divergenceStrength++;
            }
            if (!rsiInd.isLowest(lastIndex) || rsiInd.isOverbought(lastIndex)) {
                divergenceStrength++;       // RSI顶背离或超买
            }
            if (!obvmaInd.isLowest(lastIndex) || obvmaInd.isObvUTurnUp(lastIndex)) {
                divergenceStrength++;       // OBV底背离 OBV能量潮掉头
            }
        }
        tech.setDivergenceStrength(divergenceStrength);
//        log.info("-----计算顶底背离：{}", JSONObject.toJSONString(tech));

        // ------------- 量价关系 ---------------
        double prevClose = list.get(lastIndex - 1).getClose().doubleValue();
        double prevHigh = list.get(lastIndex - 1).getHigh().doubleValue();
        double high10 = list.stream().mapToDouble(b -> b.getHigh().doubleValue()).max().getAsDouble();
        calcVolumePriceRise(tech, prevClose, prevHigh, high10, lowestN.bigDecimalValue(), obvmaInd.isHighest(lastIndex));
//        log.info("-----计算量价关系：{}", JSONObject.toJSONString(tech));

        // -------------- 分时多因子共振信号:EMA、MACD、RSI、KDJ、WR、BOLL、VMACD、OBVMA及量价关系（买入和评分）----------------------
        short buyScore = 0;
        short sellScore = 0;
        List<String> buyReasons = new ArrayList<>();
        List<String> sellReasons = new ArrayList<>();
        // 一. 趋势类指标 多头基调(EMA + MACD) + 波动爆发 (BOLL)：40分
        // 1. EMA 多头排列  10分
        if (currClose.compareTo(tech.getEma5()) > 0 && ema5Num.isGreaterThan(ema10Num)) {
            buyScore += 10;
            buyReasons.add("EMA多头排列,短期强势(价格>EMA5>EMA10)");
        } else if (ema5Num.isGreaterThan(ema10Num) && ema5.getValue(lastIndex - 1).isLessThanOrEqual(ema10.getValue(lastIndex - 1))) {      // 金叉
            buyScore += 5;
            buyReasons.add("EMA金叉");   // 金叉: 当前 MA5 > MA10; 前一刻 MA5 <= MA10
//            tech.setEmaGolden(GOLDEN_CROSS);
        }
        // 2. MACD 零轴上金叉 15分
        if (macdStatus == DtMACDIndicator.CrossStatus.GOLDEN_CROSS_RED) {   // 金叉且红柱放大
            buyScore += 15;
            buyReasons.add("MACD零轴上金叉且红柱放大(动能强)");
        } else if (macdStatus == DtMACDIndicator.CrossStatus.GOLDEN_CROSS) {
            buyScore += 10;
            buyReasons.add("MACD零轴上金叉");
        }
        // 3. BOLL 突破下轨支撑 15分
        if (bollInd.isBreakoutDown(lastIndex)) {
            buyScore += 10;
            buyReasons.add("价格突破BOLL下轨(买入信号)");
        }
        // BOll "开口上涨"：中轨向上倾斜 + 布林带开口 15分
        if (bollInd.isBullishBreakout(lastIndex)) {
            buyScore += 15;
            buyReasons.add("BOLL开口且中轨向上倾斜(买入信号)");
        }

        // 二 动能类指标 灵敏择时 (KDJ + RSI + WR)：40分
        // 4. RSI 在50~70之间最强  10分
        if (rsiInd.isOversold(lastIndex)) {     // 向上反转信号
            buyScore += 10;
            buyReasons.add("RSI超卖(<30)");
        }
        // 5. KDJ 对短线拐点极其灵敏  10分
        if (tech.getKdjStatus() == DtKDJIndicator.CrossStatus.GOLDEN_CROSS) {   // 金叉
            if (k.isLessThanOrEqual(NUM_20) && d.isLessThanOrEqual(NUM_20)) {
                buyScore += 10;
                buyReasons.add("(KDJ低位金叉，强烈买入信号(K≤20)");       // 低位金叉（K<20）：代表价格超跌后的动能反转，此时买入信号最为准确。
            } else {
                buyScore += 5;
                buyReasons.add("(KDJ金叉");
            }
        }
        //  KDJ超卖区（机会显现）  10分
        if (j.isLessThanOrEqual(NUM_10)) {                            // 精准买卖点（J值比K/D更准）
            buyScore += 10;
            buyReasons.add("(KDJ严重超卖，买入信号(J≤10)");
        } else if (j.isLessThanOrEqual(NUM_20)) {
            buyScore += 5;
            buyReasons.add("(KDJ超卖，买入信号(J≤20)");
        }
        // 6. WR ≥ 80 超卖区回升并突破-50, 辅助确认超卖（避免RSI假信号）  10分
        if (wrInd.isOversold(lastIndex)) {
            buyScore += 10;
            buyReasons.add("WR超卖区，买入机会(≥80)");    // 等待信号确认,股价重新站上分时均价线时，才是安全的低吸时点。
        }


        // 三 量价类指标 量能确认 (VMACD + OBVMA)：25分
        // 7. VMACD 量能验证真伪关键   -- 隔夜条件：VMACD 红柱 → 量价配合  15分
        if (vmacdStatus == DtVMACDIndicator.CrossStatus.GOLDEN_CROSS_RED) {   // 金叉且红柱放大
            buyScore += 15;
            buyReasons.add("VMACD零轴上金叉且红柱放大(放量)");
        } else if (vmacdStatus == DtVMACDIndicator.CrossStatus.GOLDEN_CROSS) {
            buyScore += 10;
            buyReasons.add("VMACD零轴上金叉(放量)");
        }
        // 8. OBVMA 能量潮均线 -- 隔夜条件：OBV > OBV_MA5  10分
        if (tech.getObvStatus() == DtOBVMAIndicator.CrossStatus.GOLDEN_CROSS) {
            buyScore += 10;
            buyReasons.add("OBV金叉 资金流入(买入信号)");
        }
        tech.setBuyScore(buyScore);
        tech.setBuyReason(StringUtil.joinWithIndex(COMMA, buyReasons));

        // -------------- 分时多因子共振信号:EMA、MACD、RSI、KDJ、WR、BOLL、VMACD、OBVMA及量价关系（卖出和评分）----------------------
        // 一 趋势类指标 多头基调(EMA + MACD) + 波动爆发 (BOLL)：40分
        // 1. EMA空头排列  10分
        if (currClose.compareTo(tech.getEma5()) < 0 && ema5Num.isLessThan(ema10Num)) {
            sellScore += 10;
            sellReasons.add("EMA空头排列,短期弱势(价格<EMA5<EMA10)");
        } else if (ema5Num.isLessThan(ema10Num) && ema5.getValue(lastIndex - 1).isGreaterThan(ema10.getValue(lastIndex - 1))) {      // 死叉
            sellScore += 5;
            sellReasons.add("EMA死叉");
//            tech.setEmaGolden(DEATH_CROSS);
        }
        // 2. MACD 零轴下死叉 15分
        if (macdStatus == DtMACDIndicator.CrossStatus.DEATH_CROSS_GREEN) {          // 死叉且绿柱放大
            sellScore += 15;
            sellReasons.add("MACD零轴下死叉且绿柱放大(动能弱)");
        } else if (macdStatus == DtMACDIndicator.CrossStatus.DEATH_CROSS) {
            sellScore += 10;
            sellReasons.add("MACD零轴下死叉");
        }
        // 3. BOLL 突破上轨压力  15分  -- 短线止盈离场点
        if (bollInd.isBreakoutUp(lastIndex)) {
            sellScore += 10;
            sellReasons.add("价格突破BOLL上轨(卖出信号)");
        }
        //  BOLL "收口盘整"：布林带收口 + 中轨走平或下倾  15分
        if (bollInd.isConsolidation(lastIndex)) {           // 扩大超过5%才视为有效，避免微小平移干扰。
            // 小于其移动平均 → 开口收窄。价格位于中轨下方，或中轨向下倾斜
            sellScore += 15;
            sellReasons.add("BOLL开口收窄且中轨走平或下倾(卖出信号)");
        }

        // 二 动能类指标  灵敏择时 (KDJ + RSI + WR)：40分
        // 4. RSI -- 隔夜条件：RSI6 在 50~70 之间最强；>80 不隔夜。  10分
        if (rsiInd.isOverbought(lastIndex)) {        // 向下反转信号
            sellScore += 10;
            sellReasons.add("RSI超买(>70)");
        }
        // 5. KDJ高位死叉  10分
        if (tech.getKdjStatus() == DtKDJIndicator.CrossStatus.DEATH_CROSS) {                            // 死叉
            if (k.isGreaterThanOrEqual(NUM_80) && d.isGreaterThanOrEqual(NUM_80)) {
                sellScore += 10;
                sellReasons.add("(KDJ高位死叉，强烈卖出信号(K≥80)");
            } else {
                sellScore += 5;
                sellReasons.add("(KDJ死叉");
            }
        }
        //   KDJ超买区（风险积聚） 10分
        if (j.isGreaterThanOrEqual(NUM_90)) {
            sellScore += 10;
            sellReasons.add("(KDJ严重超买，卖出信号(J≥90)");
        } else if (j.isGreaterThanOrEqual(NUM_80)) {
            sellScore += 5;
            sellReasons.add("(KDJ超买，卖出信号(J≥80)");
        }
        // 6. WR ≤ 20 超买区时，系统发出首个减仓信号。  10分
        if (wrInd.isOverbought(lastIndex)) {
            sellScore += 10;
            sellReasons.add("WR超买区，卖出信号(≤20)");
        }

        // 三 量价类指标 量能确认 (VMACD + OBVMA)：25分
        // 7. VMACD（成交量MACD）   15分
        if (vmacdStatus == DtVMACDIndicator.CrossStatus.DEATH_CROSS_GREEN) {
            sellScore += 15;
            sellReasons.add("VMACD零轴下死叉且绿柱放大(缩量)");
        } else if (vmacdStatus == DtVMACDIndicator.CrossStatus.DEATH_CROSS) {
            sellScore += 10;
            sellReasons.add("VMACD零轴下死叉(缩量)");
        }
        // 8. OBVMA 能量潮均线    10分
        if (tech.getObvStatus() == DtOBVMAIndicator.CrossStatus.DEATH_CROSS) {
            sellScore += 10;
            sellReasons.add("OBV死叉 资金流出(卖出信号)");
        }
        tech.setSellScore(sellScore);
        tech.setSellReason(StringUtil.joinWithIndex(COMMA, sellReasons));

        return tech;
    }

    /**
     * KDJ  J = 3*K - 2*D
     */
    private static Num calcKdjJNum(StochasticOscillatorKIndicator k, StochasticOscillatorDIndicator d, int idx) {
        return k.getValue(idx).multipliedBy(DecimalNum.valueOf(3)).minus(d.getValue(idx).multipliedBy(DecimalNum.valueOf(2)));
    }

    private static void calcVolumePriceRise(StockTechMinute tech, double prevClose, double prevHigh, double high10, BigDecimal lowestPrice, boolean isHighestObv) {
        double currClose = tech.getClose().doubleValue();
        double currOpen = tech.getOpen().doubleValue();
        double ema5 = tech.getEma5().doubleValue();
        double ema10 = tech.getEma10().doubleValue();

        // 计算量比 (当前量 / 5日均量)
        double volumeRatio = tech.getVolumeRatio().doubleValue();
        boolean isVolUp = volumeRatio > 1.2;        // 量比>1.2视为放量
        boolean isVolDown = volumeRatio < 0.8;      // 量比<0.8视为缩量
        boolean isPriceUp = currClose > prevClose;  // 价格涨
        double body = currClose - currOpen;
        double priceChangePercent = body / currOpen * 100;  // 计算涨跌幅 (实体)
        String tag = String.format("(涨幅%.2f%% 量比%.2f),", priceChangePercent, volumeRatio);

        SignalType signalTpye = SignalType.WATCH;
        SignalLevel signalLevel = SignalLevel.NONE;
        String signalResult = "量能持平";
        int score = 1;
        List<String> reasons = Lists.newArrayList();
        if (isVolUp) {
            boolean isPositiveCandle = currClose > currOpen;    // 收盘价 > 开盘价（阳线）
            boolean isVolumeSurge = volumeRatio > 1.5;          // 放量
            // 计算上影线比例 (判断是否滞涨)
            double upperShadow = tech.getHigh().doubleValue() - currClose;
            double shadowRatio = (body == 0) ? 100 : (upperShadow / body);
            boolean hasLongUpperShadow = shadowRatio > 0.5; // 上影线超过实体一半
            if (isPriceUp) {      //  ✅量增价升 看多/持股
                if (!isPositiveCandle) {            // 基础条件：必须是阳线且放量
                    reasons.add("非阳线，不满足量增价升");
                } else if (!isVolumeSurge) {        // 成交量未显著放大
                    signalLevel = SignalLevel.WEAK;
                    reasons.add("阳线但成交量未显著放大(量比=" + String.format("%.2f", volumeRatio) + ")");
                } else {                             // 阳线且放量
                    // 进阶过滤 1: 排除“巨量滞涨” (最危险的陷阱)
                    if (volumeRatio >= 3.0 && priceChangePercent < 0.5) {
                        signalLevel = SignalLevel.LOW;
                        reasons.add("❌巨量滞涨：量比>3但涨幅小(主力可能在对倒出货！)");
                    } else if (volumeRatio >= 3.0 && hasLongUpperShadow) {
                        signalLevel = SignalLevel.LOW;
                        reasons.add("❌巨量滞涨：量比>3但上影线长(主力可能在对倒出货！)");
                    } else {
                        // --- 逻辑判断 ---
                        // 进阶过滤 2: 实体力度
                        if (priceChangePercent > 0.5) { // 1分钟线阈值，5分钟线可设为1.0
                            score += 2;
                            reasons.add("✅K线实体饱满(买盘强劲)");
                        } else {
                            reasons.add("⚠️K线实体较小(动能一般)");
                        }
                        // 进阶过滤 3: 位置判断 (突破 vs 高位)
                        if (currClose > tech.getBollUpper().doubleValue() || currClose > prevHigh) {
                            score += 2;
                            reasons.add("🚀关键位置突破(突破BOLL上轨或前高)");
                        }
                        if (tech.getBias().doubleValue() > 0.03) {      // 乖离率>3%
                            score -= 1;
                            reasons.add("⚠️乖离率过大3%，谨防冲高回落");
                        }
                        if (isHighestObv) {
                            score += 1;
                            reasons.add("🚀OBV同步创新高");
                        }

                        // 最终决策
                        if (score >= 4) {
                            signalLevel = SignalLevel.HIGHEST;
                            signalTpye = SignalType.BUY;
                            reasons.add("✅有效量增价升，主力真金白银进攻，可跟随！");
                        } else if (score == 3) {
                            signalLevel = SignalLevel.HIGH;
                            signalTpye = SignalType.BUY;
                            reasons.add("✅有效量增价升，可轻仓试错，设好止损。");
                        } else if (score == 2) {
                            signalLevel = SignalLevel.MEDIUM;
                            reasons.add("⚪普通量增价升，可轻仓试错，设好止损。");
                        } else {
                            signalLevel = SignalLevel.LOW;
                            reasons.add("信号不够强，建议观望。");
                        }
                    }
                }
                signalResult = "✅量增价升" + tag + StringUtil.joinWithIndex(COMMA, reasons);
            } else {             // ❌量增价跌  卖出/底部分批吸筹
                // --- 逻辑判断 ---
                // 基础条件：必须是阴线且放量
                if (!isPositiveCandle && !isVolumeSurge) {
                    signalLevel = SignalLevel.WEAK;
                    reasons.add("阴线但成交量未显著放大，可能是洗盘");
                } else {
                    reasons.add(String.format("✅下跌(量比%.2f)", volumeRatio));
                    reasons.add(String.format("跌幅%.2f%%", priceChangePercent));
                    // 进阶过滤 1: 破位加分 (最危险)
                    if (currClose < ema10) {
                        score += 2;
                        reasons.add("🔴跌破EMA10，趋势转空");
                    }
                    if (currClose < tech.getBollMid().doubleValue()) {
                        score += 1;
                        reasons.add("⚠️跌破BOLL中轨，弱势确认");
                    }
                    boolean isNewLow = tech.getLow().compareTo(lowestPrice) <= 0;
                    if (isNewLow) {
                        score += 1;
                        reasons.add("📉创近期新低，恐慌盘涌出");
                    }
                    // 进阶过滤 2: 巨量加分 (极度危险)
                    boolean isHugeVolume = volumeRatio > 2.5;
                    if (isHugeVolume) {
                        score += 2;
                        reasons.add("💣 巨量砸盘 (量比>2.5)，主力不计成本出逃");
                    }
                    // 进阶过滤 3: 位置判断 (高位 vs 低位)
                    // 如果是在高位 (例如距离高点<10%) 出现放量跌，分数再加2
                    if (high10 * 0.9 <= currClose && isHugeVolume) {
                        score += 2;
                        reasons.add("🔝 高位突发巨量长阴，典型的主力出货见顶信号");
                    }
                    // 最终决策
                    if (score >= 6) {
                        signalLevel = SignalLevel.HIGHEST;
                        signalTpye = SignalType.SELL;
                        reasons.add("🔴 极度危险！放量破位大跌，立即清仓，严禁抄底");
                    } else if (score >= 4) {
                        signalLevel = SignalLevel.HIGH;
                        signalTpye = SignalType.SELL;
                        reasons.add("⚠️ 警告：放量下跌，趋势转弱，建议减仓或离场");
                    } else if (score >= 2) {
                        signalLevel = SignalLevel.MEDIUM;
                        reasons.add("📉 放量杀跌，切勿急于接飞刀，等待企稳信号");
                    } else {
                        signalLevel = SignalLevel.LOW;
                        reasons.add("信号不够强，建议观望。");
                    }
                }
                signalResult = "❌量增价跌" + tag + StringUtil.joinWithIndex(COMMA, reasons);
            }

        } else if (isVolDown) {
            if (isPriceUp) {      // ⚠️ 量缩价升 减仓/警戒 可能是主力高度控盘、锁仓拉升的“黄金信号”，也可能是买盘枯竭、诱多出货的“死亡陷阱”。
                boolean hasTopDivergence = tech.getDivergenceType() == DivergenceType.TOP;
                boolean isNewHigh = tech.getHigh().doubleValue() >= high10;
                boolean isHighPosition = tech.getBias().doubleValue() >= 0.08;  // 高位(乖离率>8%)
                if (isHighPosition && isNewHigh) {      // 场景 A: 高位 + 创新高 + 背离/极度缩量 -> 危险诱多
                    signalLevel = SignalLevel.HIGH;
                    signalTpye = SignalType.SELL;
                    if (hasTopDivergence || volumeRatio < 0.5) {
                        signalLevel = SignalLevel.HIGHEST;
                        reasons.add("💣 高位量缩创新高+(背离/极度缩量)=主力诱多陷阱(立即止盈/清仓)");
                    }
                } else if (ema5 <= ema10) {    // 场景 B: 下跌趋势中的缩量涨 -> 弱势反弹
                    signalLevel = SignalLevel.HIGH;
                    signalTpye = SignalType.SELL;
                    reasons.add("⚠️下跌趋势中的无量反弹，买盘不足，观望切勿抄底，反弹随时结束");
                    if (hasTopDivergence) {     // 检测顶背离 (价格新高，MACD未新高)
                        signalLevel = SignalLevel.HIGHEST;
                        reasons.add("🔴 检测到顶背离：价格新高，但MACD动能减弱");
                    }
                } else if (!isHighPosition) {  // 场景 C: 主升浪中段 + 缩量涨 -> 良性锁仓
                    // 即使创新高，只要乖离率不大且无明显背离，视为锁仓
                    signalLevel = SignalLevel.HIGH;
                    signalTpye = SignalType.BUY;
                    if (isNewHigh) {
                        reasons.add("🚀 主升浪创新高，缩量表明抛压轻，主力锁仓良好");
                    } else {
                        reasons.add("✅ 上升趋势中缩量整理后上涨，健康信号");
                    }
                    if (hasTopDivergence) {     // 检测顶背离 (价格新高，MACD未新高)
                        signalLevel = SignalLevel.MEDIUM;
                        signalTpye = SignalType.WATCH;
                        reasons.add("🔴 检测到顶背离：价格新高，但MACD动能减弱");
                    }
                }
                signalResult = "⚠️量缩价升" + tag + (CollectionUtils.isEmpty(reasons) ? "观察，等待放量确认" : StringUtil.joinWithIndex(COMMA, reasons));
            } else {              // ⚪ 量缩价跌 持币/观望 可能是主力洗盘、散户惜售的“黄金坑”（买入机会），也可能是无人问津、阴跌不止的“无底洞”（死亡陷阱）
                //上升趋势回调中的量缩价跌 = 利好（洗盘，抛压轻，主力未出逃）。
                //下跌趋势/高位破位后的量缩价跌 = 利空（买盘枯竭，阴跌，深不见底）。

                // 支撑位：EMA20 或 BOLL下轨
                boolean atSupportBoll = currClose <= tech.getBollLower().doubleValue() * 1.01;
                // 场景 A: 上升趋势 + 回踩支撑 + 缩量 -> 良性洗盘 (GOOD_WASHOUT)
                if (currClose > ema10 && atSupportBoll) {
                    signalLevel = SignalLevel.MEDIUM;
                    signalTpye = SignalType.WATCH;
                    reasons.add("🟢 上升趋势回踩支撑(EMA10/BOLL下轨)");
                    reasons.add("✅ 成交量极度萎缩，表明主力未出逃，散户惜售");      // 关注：若次日放量阳线反包，可大胆买入
                    // 判断K线形态 (是否止跌)
                    double bodySize = (prevClose - currClose) / prevClose * 100;
                    boolean isSmallCandle = bodySize < 0.5; // 小阴线
                    double lowerShadow = tech.getLow().doubleValue() - currClose;
                    boolean hasLowerShadow = lowerShadow > (Math.abs(bodySize) * 0.5); // 有下影线
                    if (hasLowerShadow || isSmallCandle) {
                        signalLevel = SignalLevel.HIGH;
                        reasons.add("✨ K线出现止跌迹象(下影线/小阴线)，变盘在即");
                    }
                } else if (currClose < ema10) {      // 场景 B: 下跌趋势 + 无量阴跌 -> 恶性杀跌 (DANGER_BLEED)
                    signalLevel = SignalLevel.HIGHEST;
                    signalTpye = SignalType.SELL;
                    reasons.add("🔴 处于下跌趋势中(价格<EMA10)");                 // 严禁抄底：买盘枯竭，阴跌不止，深不见底
                    reasons.add("❌ 缩量下跌无承接，少量卖单即可打压股价");
                    reasons.add("⚠️ '钝刀割肉'最伤人，必须等放量止跌信号");
                } else if (prevClose > ema10 && currClose < ema10) {           // 场景 C: 高位破位后的缩量跌 -> 下跌中继 (NEUTRAL_FALL)
                    signalLevel = SignalLevel.HIGH;
                    signalTpye = SignalType.SELL;
                    reasons.add("⚠️ 刚刚跌破关键支撑(EMA10)");                   // 观望：破位初期缩量，可能是下跌中继，勿急于接飞刀
                    reasons.add("⚪ 缩量表明反弹无力，可能继续探底");
                }
                signalResult = "⚪ 量缩价跌" + tag + (CollectionUtils.isEmpty(reasons) ? "观察，等待明确信号" : StringUtil.joinWithIndex(COMMA, reasons));
            }

        }
        tech.setSignalType(signalTpye);
        tech.setSignalLevel(signalLevel);
        tech.setSignalResult(signalResult);

    }


    /**
     * 对齐东财的数值格式化（四舍五入保留2位小数）
     */
    private static double formatNum(double num) {
        // 东财用银行家舍入法（HALF_EVEN），而非默认的HALF_UP
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

}
