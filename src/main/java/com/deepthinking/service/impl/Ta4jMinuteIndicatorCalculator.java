package com.deepthinking.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.StringUtil;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.WilliamsRIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.constant.Constants.*;
import static java.math.BigDecimal.ZERO;

@Slf4j
@Service
@RequiredArgsConstructor
public class Ta4jMinuteIndicatorCalculator {


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
//        Instant begin = list.getFirst().getTradeDate().atTime(list.getFirst().getTradeTime()).atZone(ZoneId.of(ZONE_ID)).toInstant();
//        Instant end = tech.getTradeDate().atTime(tech.getTradeTime()).atZone(ZoneId.of(ZONE_ID)).toInstant();
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
        EMAIndicator ema3 = new EMAIndicator(closePriceIndicator, 3);
        EMAIndicator ema5 = new EMAIndicator(closePriceIndicator, 5);
        EMAIndicator ema10 = new EMAIndicator(closePriceIndicator, 10);
        Num ema5Num = ema5.getValue(lastIndex);
        Num ema10Num = ema10.getValue(lastIndex);
        tech.setEma3(ema3.getValue(lastIndex).bigDecimalValue());
        tech.setEma5(ema5Num.bigDecimalValue());
        tech.setEma10(ema10Num.bigDecimalValue());
        tech.setBias(currClose.subtract(tech.getEma5()).divide(tech.getEma10(), SCALE4, ROUND_MODE));

        // 2. MACD（平滑异同移动平均指数）（趋势+动能） 短线参数(5, 13, 1)   零轴确定长短周期动量方向    -- 隔夜条件：MACD红柱、DIF > DEA。
        MACDIndicator dif = new MACDIndicator(closePriceIndicator, 5, 13);       // DIF快线
        EMAIndicator dea = new EMAIndicator(dif, 1);                    // 信号线 DEA慢线
        Num difNum = dif.getValue(lastIndex);
        Num deaNum = dea.getValue(lastIndex);
        Num histNum = difNum.minus(deaNum);                                     // 柱状图 (Histogram) = MACD线 - 信号线
        Num prevHist = dif.getValue(lastIndex - 1).minus(dea.getValue(lastIndex));
        tech.setMacdDif(difNum.bigDecimalValue());
        tech.setMacdDea(deaNum.bigDecimalValue());
        tech.setMacdBar(histNum.bigDecimalValue());

        // 3. BOLL（布林带）短线参数：10 2  衡量价格相对于波动的边界位置   -- 隔夜条件：价格在中轨之上，可持仓过夜，若跌破中轨则需离场
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePriceIndicator, 10);
        BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(new SMAIndicator(closePriceIndicator, 10));
        BollingerBandsUpperIndicator upperBB = new BollingerBandsUpperIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        BollingerBandsLowerIndicator lowerBB = new BollingerBandsLowerIndicator(middleBB, stdDev, DecimalNum.valueOf(2));
        Num mid = middleBB.getValue(lastIndex);
        Num upper = upperBB.getValue(lastIndex);
        Num lower = lowerBB.getValue(lastIndex);
        tech.setBollMid(mid.bigDecimalValue());
        tech.setBollUpper(upper.bigDecimalValue());
        tech.setBollLower(lower.bigDecimalValue());
        // 开口状态: 指上轨与下轨之间的宽度（即带宽），带宽 = 上轨值-下轨值，
        Num currBand = upper.minus(lower);
        Num bandWidthPct = currBand.dividedBy(mid).multipliedBy(DecimalNum.valueOf(100));   // 带宽指标（Bandwidth）是量化“开口”的核心公式，数值越大代表开口越宽，波动越剧烈, 用于不同价格水平间的比较：
        Num avgBand = DecimalNum.valueOf(0);
        for (int i = lastIndex; i >= lastIndex - 4; i--) {
            avgBand = avgBand.plus(upperBB.getValue(i).minus(lowerBB.getValue(i)));
        }
        avgBand = avgBand.dividedBy(DecimalNum.valueOf(5));

        // 4. RSI（相对强弱指标） 超短线最灵：6    衡量市场强弱与超买超卖
        RSIIndicator rsi6 = new RSIIndicator(closePriceIndicator, 6);
        Num rsi6Num = rsi6.getValue(lastIndex);
        tech.setRsi6(rsi6Num.bigDecimalValue());

        // 5. KDJ（随机指标）短线参数：5 2 2   对短线拐点极其灵敏    -- 隔夜条件：J 在 50~80 之间最稳；J>90 不隔夜。
        // 周期：计算RSV（未成熟随机值）的周期，分时越小，N越小（如 1 分钟取 5）； RSV = (当前价 - N周期最低价) / (N周期最高价 - N周期最低价) × 100；
        // K 值平滑：K 线是 RSV 的 M1 日移动平均，分时固定取 2/3；             K = 2/3×前一日K值 + 1/3×当日RSV（初始 K=50）；
        // D 值平滑：D 线是 K 线的 M2 日移动平均，分时固定取 2/3；             D = 2/3×前一日D值 + 1/3×当日K值（初始 D=50）；
        // J 值：公式固定为 J = 3*K - 2*D（无参数）。                        J = 3×K - 2×D（J 值范围通常 ±100，超 80 = 超买，低于 20 = 超卖）
        StochasticOscillatorKIndicator stoch = new StochasticOscillatorKIndicator(series, 5);
        SMAIndicator k = new SMAIndicator(stoch, 2);        // K = SMA(RSV, kPeriod)
        SMAIndicator d = new SMAIndicator(k, 2);            // D = SMA(K, dPeriod)
        Num kNum = k.getValue(lastIndex);
        Num dNum = d.getValue(lastIndex);
        Num jNum = calcKdjJNum(k, d, lastIndex);                     // J = 3*K - 2*D
        tech.setKdjK(kNum.bigDecimalValue());
        tech.setKdjD(dNum.bigDecimalValue());
        tech.setKdjJ(jNum.bigDecimalValue());

        // 6. WR（威廉指标）极短线参数：6   用于1分钟或5分钟线，适合捕捉极速脉冲行情，预判趋势衰减      -- 隔夜条件：WR < 20 超买 → 不隔夜; WR > 80 超卖 → 可低吸隔夜; WR从超卖区回升时配合OBV放量可加仓。
        WilliamsRIndicator wr = new WilliamsRIndicator(series, 6);
        Num wrNum = wr.getValue(lastIndex);
        tech.setWr6(wrNum.bigDecimalValue());

        // 7. VMACD（成交量MACD）  短线参数：5,13,1   量平滑异同平均，量化资金动能    -- 隔夜条件：VMACD 红柱 → 量价配合
        MACDIndicator vDif = new MACDIndicator(volumeIndicator, 5, 13);
        EMAIndicator vDea = new EMAIndicator(vDif, 1);
        Num vDifNum = vDif.getValue(lastIndex);
        Num vDeaNum = vDea.getValue(lastIndex);
        Num vHistNum = vDifNum.minus(vDeaNum);         // MACD柱：DIFF与DEA的差值，反映量能动能
        Num vPrevHist = vDif.getValue(lastIndex - 1).minus(vDea.getValue(lastIndex-1));
        tech.setVmacdDif(vDifNum.bigDecimalValue());
        tech.setVmacdDea(vDeaNum.bigDecimalValue());
        tech.setVmacdBar(vHistNum.bigDecimalValue());

        // 8. OBV_MA 能量潮均线确认资金流入流出     -- 隔夜条件：OBV > OBV_MA5
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(series);
        SMAIndicator obvMa5 = new SMAIndicator(obv, 5);
        Num obvNum = obv.getValue(lastIndex);
        Num obvMa5Num = obvMa5.getValue(lastIndex);
        tech.setObv(obvNum.longValue());
        tech.setObvMa5(obvMa5Num.longValue());

        log.info("-----计算分时指标：{}", JSONObject.toJSONString(tech));

        // ----------- 顶底背离 ---------------
        BigDecimal highestPrice = tech.getHigh();
        BigDecimal lowestPrice = tech.getLow();
        BigDecimal maxDif = tech.getMacdDif();
        BigDecimal minDif = tech.getMacdDif();
        BigDecimal highestKdjk = tech.getKdjK();
        BigDecimal lowestKdjk = tech.getKdjK();
        BigDecimal highestKdjJ = tech.getKdjJ();
        BigDecimal lowKestdjJ = tech.getKdjJ();
        BigDecimal highestRsi = tech.getRsi6();
        BigDecimal lowestRsi = tech.getRsi6();
        long highestObv = tech.getObv();
        long lowestObv = tech.getObv();
        for (int i = size / 2; i < size; i++) {     //  计算前5个周期
            StockTechMinute minute = list.get(i);
            BigDecimal j = calcKdjJNum(k, d, i).bigDecimalValue();
            highestPrice = highestPrice.max(minute.getHigh());
            lowestPrice = lowestPrice.min(minute.getLow());
            highestKdjJ = highestKdjJ.max(j);
            lowKestdjJ = lowKestdjJ.min(j);
            if(!dif.getValue(i).isNaN()) {
                maxDif = maxDif.max(dif.getValue(i).bigDecimalValue());
                minDif = minDif.min(dif.getValue(i).bigDecimalValue());
            }
            highestKdjk = highestKdjk.max(k.getValue(i).bigDecimalValue());
            lowestKdjk = lowestKdjk.min(k.getValue(i).bigDecimalValue());
            highestRsi = highestRsi.max(rsi6.getValue(i).bigDecimalValue());
            lowestRsi = lowestRsi.min(rsi6.getValue(i).bigDecimalValue());
            highestObv = Math.max(highestObv, obv.getValue(i).longValue());
            lowestObv = Math.min(lowestObv, obv.getValue(i).longValue());
        }
        tech.setDivergenceType(DIVERGENCE_NONE);
        short divergenceStrength = 0;
        // 背离（Divergence）是指价格走势与动量指标（如 MACD、RSI、KDJ、OBV）的趋势方向相反，暗示当前动能正在放缓。
        if (currClose.compareTo(highestPrice) == 0 && tech.getMacdDif().compareTo(maxDif) < 0) {
            tech.setDivergenceType(DIVERGENCE_TOP);
            divergenceStrength++;           // 基础分：VMACD顶背离
            if (vHistNum.isPositiveOrZero() && vHistNum.isLessThan(vPrevHist)) {
                divergenceStrength++;       // VMACD红柱缩小
            }
            if (tech.getKdjK().compareTo(highestKdjk) < 0 || tech.getKdjJ().compareTo(highestKdjJ) < 0) {
                divergenceStrength++;       // KDJ-K顶背离 KDJ-J未新高 → +1分
            }
            if (tech.getRsi6().compareTo(highestRsi) < 0 || tech.getRsi6().compareTo(BigDecimal.valueOf(70)) > 0) {
                divergenceStrength++;       // RSI顶背离或超买
            }
            if (tech.getObv().compareTo(highestObv) < 0 || obvNum.isLessThan(obv.getValue(lastIndex - 1))) {
                divergenceStrength++;       // OBV顶背离 OBV能量潮掉头
            }

        } else if (currClose.compareTo(lowestPrice) == 0 && tech.getMacdDif().compareTo(minDif) > 0) {
            tech.setDivergenceType(DIVERGENCE_BOTTOM);
            divergenceStrength++;
            if (vHistNum.isNegativeOrZero() && vHistNum.isLessThan(vPrevHist)) {
                divergenceStrength++;       // VMACD绿柱缩小
            }
            if (tech.getKdjK().compareTo(lowestKdjk) > 0 || tech.getKdjJ().compareTo(lowestKdjk) > 0) {
                divergenceStrength++;       // KDJ-K底背离 KDJ-J未新低 → +1分
            }
            if (tech.getRsi6().compareTo(lowestRsi) > 0 || tech.getRsi6().compareTo(BigDecimal.valueOf(30)) < 0) {
                divergenceStrength++;       // RSI顶背离或超买
            }
            if (tech.getObv().compareTo(lowestObv) > 0 || obvNum.isGreaterThan(obv.getValue(lastIndex - 1))) {
                divergenceStrength++;       // OBV底背离 OBV能量潮掉头
            }
        }
        tech.setDivergenceStrength(divergenceStrength);
        log.info("-----计算顶底背离：{}", JSONObject.toJSONString(tech));

        // ------------- 量价关系 ---------------
        double prevClose = closePriceIndicator.getValue(lastIndex - 1).doubleValue();
        double prevHigh = new HighPriceIndicator(series).getValue(lastIndex - 1).doubleValue();
        double high10 = list.stream().mapToDouble(b -> b.getHigh().doubleValue()).max().getAsDouble();
        calcVolumePriceRise(tech, prevClose, prevHigh, high10, lowestPrice, highestObv);
        log.info("-----计算量价关系：{}", JSONObject.toJSONString(tech));

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
            tech.setEmaGolden(GOLDEN_CROSS);
        }
        // 2. MACD 零轴上金叉 15分
        if (difNum.isPositiveOrZero() && difNum.isGreaterThan(deaNum)) {
            if (histNum.isPositive() && histNum.isGreaterThan(prevHist)) {      // 金叉且红柱放大
                buyScore += 15;
                buyReasons.add("MACD零轴上金叉且红柱放大(动能强)");
                tech.setMacdGolden(GOLDEN_CROSS_RED);
            } else {
                buyScore += 10;
                buyReasons.add("MACD零轴上金叉");
                tech.setMacdGolden(GOLDEN_CROSS);
            }
        }
        // 3. BOLL 突破下轨支撑 15分
        if (currClose.compareTo(tech.getBollLower()) <= 0) {
            buyScore += 10;
            buyReasons.add("价格突破BOLL下轨(买入信号)");
        }
        // BOll 开口扩大向上且价格位于中轨上方 15分
        if (bandWidthPct.isGreaterThanOrEqual(DecimalNum.valueOf(5)) && currBand.isGreaterThan(avgBand)) {  // 扩大超过5%才视为有效，避免微小平移干扰。
            // 当前带宽大于其移动平均 → 开口扩张； 价格位于中轨上方，或中轨向上倾斜 → 开口扩大向上
            if (currClose.compareTo(mid.bigDecimalValue()) > 0 || mid.isGreaterThan(middleBB.getValue(lastIndex - 1))) {
                buyScore += 15;
                buyReasons.add("开口扩大向上且价格位于中轨上方(或中轨上斜)");
                tech.setBollExpandStatus(EXPAND);
            }
        }

        // 二 动能类指标 灵敏择时 (KDJ + RSI + WR)：40分
        // 4. RSI 在50~70之间最强  10分
        if (tech.getRsi6().compareTo(BigDecimal.valueOf(30)) < 0) {     // 向上反转信号
            buyScore += 10;
            buyReasons.add("RSI超卖(<30)");
        }
        // 5. KDJ 对短线拐点极其灵敏  10分
        if (kNum.isGreaterThan(dNum) && k.getValue(lastIndex - 1).isLessThanOrEqual(d.getValue(lastIndex - 1))) {   // 金叉
            if (kNum.isLessThanOrEqual(DecimalNum.valueOf(20)) && dNum.isLessThanOrEqual(DecimalNum.valueOf(20))) {
                buyScore += 10;
                buyReasons.add("(KDJ低位金叉，强烈买入信号(K≤20)");       // 低位金叉（K<20）：代表价格超跌后的动能反转，此时买入信号最为准确。
                tech.setKdjGolden(GOLDEN_CROSS_RED);
            } else {
                buyScore += 5;
                buyReasons.add("(KDJ金叉");
                tech.setKdjGolden(GOLDEN_CROSS);
            }
        }
        //  KDJ超卖区（机会显现）  10分
        if (tech.getKdjJ().compareTo(BigDecimal.valueOf(10)) <= 0) {    // 精准买卖点（J值比K/D更准）
            buyScore += 10;
            buyReasons.add("(KDJ严重超卖，买入信号(J≤10)");
        } else if (tech.getKdjJ().compareTo(BigDecimal.valueOf(20)) <= 0) {
            buyScore += 5;
            buyReasons.add("(KDJ超卖，买入信号(J≤20)");
        }
        // 6. WR 从-80以下超卖区回升并突破-50, 辅助确认超卖（避免RSI假信号）  10分
        if (tech.getWr6().compareTo(BigDecimal.valueOf(-80)) <= 0) {
            buyScore += 10;
            buyReasons.add("WR超卖区，买入机会(≤-80)");    // 等待信号确认,股价重新站上分时均价线时，才是安全的低吸时点。
        }


        // 三 量价类指标 量能确认 (VMACD + OBVMA)：25分
        // 7. VMACD 量能验证真伪关键   -- 隔夜条件：VMACD 红柱 → 量价配合  15分
        if (vHistNum.isPositiveOrZero() && vDifNum.isGreaterThan(vDeaNum)) {
            if (vHistNum.isPositiveOrZero() && vHistNum.isGreaterThan(vPrevHist)) {     // 金叉且红柱放大
                buyScore += 15;
                buyReasons.add("VMACD零轴上金叉且红柱放大(放量)");
                tech.setVmacdGolden(GOLDEN_CROSS_RED);
            } else {
                buyScore += 10;
                buyReasons.add("VMACD零轴上金叉(放量)");
                tech.setVmacdGolden(GOLDEN_CROSS);
            }
        }
        // 8. OBVMA 能量潮均线 -- 隔夜条件：OBV > OBV_MA5  10分
        if (obvNum.isGreaterThan(obvMa5Num) && obv.getValue(lastIndex - 1).isLessThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            buyScore += 10;
            buyReasons.add("OBV金叉 资金流入(买入信号)");
            tech.setObvGolden(GOLDEN_CROSS);
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
            tech.setEmaGolden(DEATH_CROSS);
        }
        // 2. MACD 零轴下死叉 15分
        if (difNum.isNegative() && difNum.isLessThan(deaNum)) {
            if (histNum.isNegative() && histNum.isLessThan(prevHist)) {         // 死叉且绿柱放大
                sellScore += 15;
                sellReasons.add("MACD零轴下死叉且绿柱放大(动能弱)");
                tech.setMacdGolden(DEATH_CROSS_GREEN);
            } else {
                sellScore += 10;
                sellReasons.add("MACD零轴下死叉");
                tech.setMacdGolden(DEATH_CROSS);
            }
        }
        // 3. BOLL 突破上轨压力  15分  -- 短线止盈离场点
        if (currClose.compareTo(tech.getBollUpper()) >= 0) {
            sellScore += 10;
            sellReasons.add("价格突破BOLL上轨(卖出信号)");
        }
        //  BOLL 开口收窄向下且价格位于中轨下方  15分
        if (bandWidthPct.isGreaterThanOrEqual(DecimalNum.valueOf(5)) && currBand.isLessThan(avgBand)) {  // 扩大超过5%才视为有效，避免微小平移干扰。
            // 小于其移动平均 → 开口收窄。价格位于中轨下方，或中轨向下倾斜
            if (currClose.compareTo(mid.bigDecimalValue()) < 0 || mid.isLessThan(middleBB.getValue(lastIndex - 1))) {
                sellScore += 15;
                sellReasons.add("开口收窄向下且价格位于中轨下方(或中轨下斜)");
                tech.setBollExpandStatus(SHRINK);
            }
        }

        // 二 动能类指标  灵敏择时 (KDJ + RSI + WR)：40分
        // 4. RSI -- 隔夜条件：RSI6 在 50~70 之间最强；>80 不隔夜。  10分
        if (tech.getRsi6().compareTo(BigDecimal.valueOf(70)) > 0) {        // 向下反转信号
            sellScore += 10;
            sellReasons.add("RSI超买(>70)");
        }
        // 5. KDJ高位死叉  10分
        if (kNum.isLessThan(dNum) && k.getValue(lastIndex - 1).isGreaterThanOrEqual(d.getValue(lastIndex - 1))) {   // 死叉
            if (kNum.isGreaterThanOrEqual(DecimalNum.valueOf(80)) && dNum.isGreaterThanOrEqual(DecimalNum.valueOf(80))) {
                sellScore += 10;
                sellReasons.add("(KDJ高位死叉，强烈卖出信号(K≥80)");
                tech.setKdjGolden(DEATH_CROSS_GREEN);
            } else {
                sellScore += 5;
                sellReasons.add("(KDJ死叉");
                tech.setKdjGolden(DEATH_CROSS);
            }
        }
        //   KDJ超买区（风险积聚） 10分
        if (tech.getKdjJ().compareTo(BigDecimal.valueOf(90)) >= 0) {
            sellScore += 10;
            sellReasons.add("(KDJ严重超买，卖出信号(J≥90)");
        } else if (tech.getKdjJ().compareTo(BigDecimal.valueOf(80)) >= 0) {
            sellScore += 5;
            sellReasons.add("(KDJ超买，卖出信号(J≥80)");
        }
        // 6. WR 指标率先进入-20以上超买区时，系统发出首个减仓信号。  10分
        if (tech.getWr6().compareTo(BigDecimal.valueOf(-20)) > 0) {
            sellScore += 10;
            sellReasons.add("WR超买区，卖出信号(>-20)");
        }

        // 三 量价类指标 量能确认 (VMACD + OBVMA)：25分
        // 7. VMACD（成交量MACD）   15分
        if (vDifNum.isNegative() && vDifNum.isLessThan(vDeaNum)) {
            if (vHistNum.isNegativeOrZero() && vHistNum.isGreaterThan(vPrevHist)) {
                sellScore += 15;
                sellReasons.add("VMACD零轴下死叉且绿柱放大(缩量)");
            } else {
                sellScore += 10;
                sellReasons.add("VMACD零轴下死叉(缩量)");
            }
        }
        // 8. OBVMA 能量潮均线    10分
        if (obvNum.isLessThan(obvMa5Num) && obv.getValue(lastIndex - 1).isGreaterThanOrEqual(obvMa5.getValue(lastIndex - 1))) {
            sellScore += 10;
            sellReasons.add("OBV死叉 资金流出(卖出信号)");
            tech.setObvGolden(DEATH_CROSS);
        }
        tech.setSellScore(sellScore);
        tech.setSellReason(StringUtil.joinWithIndex(COMMA, sellReasons));
        log.info("-----计算信号共振：{}", JSONObject.toJSONString(tech));

        return tech;
    }

    /**
     * KDJ  J = 3*K - 2*D
     */
    private static Num calcKdjJNum(SMAIndicator k, SMAIndicator d, int idx) {
        return k.getValue(idx).multipliedBy(DecimalNum.valueOf(3)).minus(d.getValue(idx).multipliedBy(DecimalNum.valueOf(2)));
    }

    private static void calcVolumePriceRise(StockTechMinute tech, double prevClose, double prevHigh, double high10, BigDecimal lowestPrice, long highestObv) {
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

        short signalLevel = SIGNAL_NONE;
        short signalTpye = OPERATING_WATCH;
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
                    signalLevel = SIGNAL_WEAK;
                    reasons.add("阳线但成交量未显著放大(量比=" + String.format("%.2f", volumeRatio) + ")");
                } else {                             // 阳线且放量
                    // 进阶过滤 1: 排除“巨量滞涨” (最危险的陷阱)
                    if (volumeRatio >= 3.0 && priceChangePercent < 0.5) {
                        signalLevel = SIGNAL_LOW;
                        reasons.add("❌巨量滞涨：量比>3但涨幅小(主力可能在对倒出货！)");
                    } else if (volumeRatio >= 3.0 && hasLongUpperShadow) {
                        signalLevel = SIGNAL_LOW;
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
                        if (tech.getObv() >= highestObv) {
                            score += 1;
                            reasons.add("🚀OBV同步创新高");
                        }

                        // 最终决策
                        if (score >= 4) {
                            signalLevel = SIGNAL_HIGHEST;
                            signalTpye = OPERATING_BUY;
                            reasons.add("✅有效量增价升，主力真金白银进攻，可跟随！");
                        } else if (score == 3) {
                            signalLevel = SIGNAL_HIGH;
                            signalTpye = OPERATING_BUY;
                            reasons.add("✅有效量增价升，可轻仓试错，设好止损。");
                        } else if (score == 2) {
                            signalLevel = SIGNAL_MEDIUM;
                            reasons.add("⚪普通量增价升，可轻仓试错，设好止损。");
                        } else {
                            signalLevel = SIGNAL_LOW;
                            reasons.add("信号不够强，建议观望。");
                        }
                    }
                }
                signalResult = "✅量增价升" + tag + StringUtil.joinWithIndex(COMMA, reasons);
            } else {             // ❌量增价跌  卖出/底部分批吸筹
                // --- 逻辑判断 ---
                // 基础条件：必须是阴线且放量
                if (!isPositiveCandle && !isVolumeSurge) {
                    signalLevel = SIGNAL_WEAK;
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
                        signalLevel = SIGNAL_HIGHEST;
                        signalTpye = OPERATING_SELL;
                        reasons.add("🔴 极度危险！放量破位大跌，立即清仓，严禁抄底");
                    } else if (score >= 4) {
                        signalLevel = SIGNAL_HIGH;
                        signalTpye = OPERATING_SELL;
                        reasons.add("⚠️ 警告：放量下跌，趋势转弱，建议减仓或离场");
                    } else if (score >= 2) {
                        signalLevel = SIGNAL_MEDIUM;
                        reasons.add("📉 放量杀跌，切勿急于接飞刀，等待企稳信号");
                    } else {
                        signalLevel = SIGNAL_LOW;
                        reasons.add("信号不够强，建议观望。");
                    }
                }
                signalResult = "❌量增价跌" + tag + StringUtil.joinWithIndex(COMMA, reasons);
            }

        } else if (isVolDown) {
            if (isPriceUp) {      // ⚠️ 量缩价升 减仓/警戒 可能是主力高度控盘、锁仓拉升的“黄金信号”，也可能是买盘枯竭、诱多出货的“死亡陷阱”。
                boolean hasTopDivergence = tech.getDivergenceType() == DIVERGENCE_TOP;
                boolean isNewHigh = tech.getHigh().doubleValue() >= high10;
                boolean isHighPosition = tech.getBias().doubleValue() >= 0.08;  // 高位(乖离率>8%)
                if (isHighPosition && isNewHigh) {      // 场景 A: 高位 + 创新高 + 背离/极度缩量 -> 危险诱多
                    signalLevel = SIGNAL_HIGH;
                    signalTpye = OPERATING_SELL;
                    if (hasTopDivergence || volumeRatio < 0.5) {
                        signalLevel = SIGNAL_HIGHEST;
                        reasons.add("💣 高位量缩创新高+(背离/极度缩量)=主力诱多陷阱(立即止盈/清仓)");
                    }
                } else if (ema5 <= ema10) {    // 场景 B: 下跌趋势中的缩量涨 -> 弱势反弹
                    signalLevel = SIGNAL_HIGH;
                    signalTpye = OPERATING_SELL;
                    reasons.add("⚠️下跌趋势中的无量反弹，买盘不足，观望切勿抄底，反弹随时结束");
                    if (hasTopDivergence) {     // 检测顶背离 (价格新高，MACD未新高)
                        signalLevel = SIGNAL_HIGHEST;
                        reasons.add("🔴 检测到顶背离：价格新高，但MACD动能减弱");
                    }
                } else if (!isHighPosition) {  // 场景 C: 主升浪中段 + 缩量涨 -> 良性锁仓
                    // 即使创新高，只要乖离率不大且无明显背离，视为锁仓
                    signalLevel = SIGNAL_HIGH;
                    signalTpye = OPERATING_BUY;
                    if (isNewHigh) {
                        reasons.add("🚀 主升浪创新高，缩量表明抛压轻，主力锁仓良好");
                    } else {
                        reasons.add("✅ 上升趋势中缩量整理后上涨，健康信号");
                    }
                    if (hasTopDivergence) {     // 检测顶背离 (价格新高，MACD未新高)
                        signalLevel = SIGNAL_MEDIUM;
                        signalTpye = OPERATING_WATCH;
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
                    signalLevel = SIGNAL_MEDIUM;
                    signalTpye = OPERATING_WATCH;
                    reasons.add("🟢 上升趋势回踩支撑(EMA10/BOLL下轨)");
                    reasons.add("✅ 成交量极度萎缩，表明主力未出逃，散户惜售");      // 关注：若次日放量阳线反包，可大胆买入
                    // 判断K线形态 (是否止跌)
                    double bodySize = (prevClose - currClose) / prevClose * 100;
                    boolean isSmallCandle = bodySize < 0.5; // 小阴线
                    double lowerShadow = tech.getLow().doubleValue() - currClose;
                    boolean hasLowerShadow = lowerShadow > (Math.abs(bodySize) * 0.5); // 有下影线
                    if (hasLowerShadow || isSmallCandle) {
                        signalLevel = SIGNAL_HIGH;
                        reasons.add("✨ K线出现止跌迹象(下影线/小阴线)，变盘在即");
                    }
                } else if (currClose < ema10) {      // 场景 B: 下跌趋势 + 无量阴跌 -> 恶性杀跌 (DANGER_BLEED)
                    signalLevel = SIGNAL_HIGHEST;
                    signalTpye = OPERATING_SELL;
                    reasons.add("🔴 处于下跌趋势中(价格<EMA10)");                 // 严禁抄底：买盘枯竭，阴跌不止，深不见底
                    reasons.add("❌ 缩量下跌无承接，少量卖单即可打压股价");
                    reasons.add("⚠️ '钝刀割肉'最伤人，必须等放量止跌信号");
                } else if (prevClose > ema10 && currClose < ema10) {           // 场景 C: 高位破位后的缩量跌 -> 下跌中继 (NEUTRAL_FALL)
                    signalLevel = SIGNAL_HIGH;
                    signalTpye = OPERATING_SELL;
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


}
