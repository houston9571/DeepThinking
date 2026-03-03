package com.deepthinking.service.impl;

import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.deepthinking.common.constant.Constants.ROUND_MODE;
import static com.deepthinking.common.constant.Constants.SCALE4;

/**
 * 分时指标计算器（1-3天超短线专用）
 * 入参：按时间正序的分钟数据
 */
public class StockIndicatorMinuteCalculator {


    /**
     * 分时共振判断工具（1-3天超短线专用）
     * 规则和日线共振对齐，适配分时高频、短周期特点
     */
    // 共振信号常量（和日线保持一致）
    public static final int RESONANCE_NONE = 0;      // 无信号
    public static final int RESONANCE_BUY = 1;       // 强力买入
    public static final int RESONANCE_SELL = 2;      // 强力卖出
    public static final int RESONANCE_TREND_UP = 3;  // 趋势走强
    public static final int RESONANCE_TREND_DOWN = 4;// 趋势走弱



    // ==================== 1. 分时MA ====================
    public static List<BigDecimal> calcMa(List<BigDecimal> prices, int period) {
        List<BigDecimal> maList = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                maList.add(null);
                continue;
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                sum = sum.add(prices.get(j));
            }
            maList.add(sum.divide(BigDecimal.valueOf(period), SCALE4, ROUND_MODE));
        }
        return maList;
    }

    // ==================== 2. 分时MACD(5,13,1) ====================
    public static List<BigDecimal[]> calcMacd(List<BigDecimal> prices) {
        int emaFast = 5, emaSlow = 13, emaDea = 1;
        List<BigDecimal> ema5 = calcEma(prices, emaFast);
        List<BigDecimal> ema13 = calcEma(prices, emaSlow);
        List<BigDecimal> difList = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (ema5.get(i) == null || ema13.get(i) == null) {
                difList.add(null);
            } else {
                difList.add(ema5.get(i).subtract(ema13.get(i)));
            }
        }

        List<BigDecimal> deaList = calcEma(difList, emaDea);
        List<BigDecimal[]> macdList = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            BigDecimal dif = difList.get(i);
            BigDecimal dea = deaList.get(i);
            if (dif == null || dea == null) {
                macdList.add(new BigDecimal[]{null, null, null});
            } else {
                BigDecimal bar = dif.subtract(dea).multiply(BigDecimal.valueOf(2));
                macdList.add(new BigDecimal[]{dif, dea, bar});
            }
        }
        return macdList;
    }

    // ==================== 3. 分时RSI(3/9) ====================
    public static List<BigDecimal> calcRsi(List<BigDecimal> prices, int period) {
        List<BigDecimal> rsiList = new ArrayList<>();
        if (prices.size() < period) return rsiList;

        for (int i = 0; i < prices.size(); i++) {
            if (i < period) {
                rsiList.add(null);
                continue;
            }
            BigDecimal up = BigDecimal.ZERO;
            BigDecimal down = BigDecimal.ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                BigDecimal chg = prices.get(j).subtract(prices.get(j - 1));
                if (chg.compareTo(BigDecimal.ZERO) > 0) up = up.add(chg);
                else down = down.add(chg.abs());
            }
            BigDecimal rsi = up.divide(up.add(down), SCALE4, ROUND_MODE).multiply(BigDecimal.valueOf(100));
            rsiList.add(rsi);
        }
        return rsiList;
    }

    // ==================== 4. 分时KDJ(5) ====================
    public static List<BigDecimal[]> calcKdj(List<BigDecimal> highs, List<BigDecimal> lows, List<BigDecimal> closes) {
        int period = 5;
        List<BigDecimal[]> kdjList = new ArrayList<>();
        BigDecimal k0 = BigDecimal.valueOf(50);
        BigDecimal d0 = BigDecimal.valueOf(50);

        for (int i = 0; i < closes.size(); i++) {
            if (i < period - 1) {
                kdjList.add(new BigDecimal[]{null, null, null});
                continue;
            }
            BigDecimal h = highs.get(i);
            BigDecimal l = lows.get(i);
            for (int j = i - period + 1; j <= i; j++) {
                h = h.max(highs.get(j));
                l = l.min(lows.get(j));
            }
            BigDecimal close = closes.get(i);
            BigDecimal rsv = close.subtract(l).divide(h.subtract(l), SCALE4, ROUND_MODE).multiply(BigDecimal.valueOf(100));
            BigDecimal k = k0.multiply(BigDecimal.valueOf(2)).add(rsv).divide(BigDecimal.valueOf(3), SCALE4, ROUND_MODE);
            BigDecimal d = d0.multiply(BigDecimal.valueOf(2)).add(k).divide(BigDecimal.valueOf(3), SCALE4, ROUND_MODE);
            BigDecimal j = k.multiply(BigDecimal.valueOf(3)).subtract(d.multiply(BigDecimal.valueOf(2)));
            kdjList.add(new BigDecimal[]{k, d, j});
            k0 = k;
            d0 = d;
        }
        return kdjList;
    }

    // ==================== 内部EMA ====================
    private static List<BigDecimal> calcEma(List<BigDecimal> values, int period) {
        List<BigDecimal> emaList = new ArrayList<>();
        BigDecimal factor = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = null;

        for (int i = 0; i < values.size(); i++) {
            BigDecimal v = values.get(i);
            if (v == null) {
                emaList.add(null);
                continue;
            }
            if (ema == null) ema = v;
            else ema = v.multiply(factor).add(ema.multiply(BigDecimal.ONE.subtract(factor)));
            emaList.add(ema.setScale(SCALE4, ROUND_MODE));
        }
        return emaList;
    }

    // ==================== 5. 分时WR6（威廉指标） ====================
    public static List<BigDecimal> calcWr6(List<BigDecimal> highs, List<BigDecimal> lows, List<BigDecimal> closes) {
        int period = 6;
        List<BigDecimal> wrList = new ArrayList<>();
        for (int i = 0; i < closes.size(); i++) {
            if (i < period - 1) {
                wrList.add(null);
                continue;
            }
            // 取近6分钟最高价/最低价
            BigDecimal high = highs.get(i);
            BigDecimal low = lows.get(i);
            for (int j = i - period + 1; j <= i; j++) {
                high = high.max(highs.get(j));
                low = low.min(lows.get(j));
            }
            BigDecimal close = closes.get(i);
            // WR计算公式：(最高价 - 当前价)/(最高价 - 最低价) * -100
            BigDecimal wr = high.subtract(close).divide(high.subtract(low), SCALE4, ROUND_MODE).multiply(BigDecimal.valueOf(-100));
            wrList.add(wr);
        }
        return wrList;
    }

    // ==================== 6. 分时BOLL10（布林带） ====================
    public static List<BigDecimal[]> calcBoll(List<BigDecimal> closes) {
        int period = 10;
        List<BigDecimal[]> bollList = new ArrayList<>();
        List<BigDecimal> ma10 = calcMa(closes, period); // 中轨=MA10

        for (int i = 0; i < closes.size(); i++) {
            if (i < period - 1) {
                bollList.add(new BigDecimal[]{null, null, null});
                continue;
            }
            // 计算标准差
            BigDecimal avg = ma10.get(i);
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                sum = sum.add(closes.get(j).subtract(avg).pow(2));
            }
            BigDecimal std = BigDecimal.valueOf(Math.sqrt(sum.divide(BigDecimal.valueOf(period), SCALE4, ROUND_MODE).doubleValue()));
            // 上轨=中轨+2*标准差，下轨=中轨-2*标准差
            BigDecimal upper = avg.add(std.multiply(BigDecimal.valueOf(2)));
            BigDecimal lower = avg.subtract(std.multiply(BigDecimal.valueOf(2)));
            bollList.add(new BigDecimal[]{avg, upper, lower});
        }
        return bollList;
    }

    // ==================== 7. 分时VMACD(5,13,1)（量能MACD） ====================
    public static List<BigDecimal[]> calcVmacd(List<Long> volumes) {
        // 转换成交量为BigDecimal
        List<BigDecimal> volList = new ArrayList<>();
        for (Long vol : volumes) {
            volList.add(BigDecimal.valueOf(vol));
        }
        // 复用MACD计算逻辑
        return calcMacd(volList);
    }

    // ==================== 8. 分时OBV及OBV_MA5 ====================
    public static List<Long> calcObv(List<BigDecimal> closes, List<Long> volumes) {
        List<Long> obvList = new ArrayList<>();
        long obv = 0;
        for (int i = 0; i < closes.size(); i++) {
            if (i == 0) {
                obv = volumes.get(i); // 第一分钟OBV=成交量
            } else {
                // 价格上涨：OBV+=成交量；下跌：OBV-=成交量；平盘：不变
                int compare = closes.get(i).compareTo(closes.get(i - 1));
                if (compare > 0) {
                    obv += volumes.get(i);
                } else if (compare < 0) {
                    obv -= volumes.get(i);
                }
            }
            obvList.add(obv);
        }
        return obvList;
    }

    // 计算OBV MA5
    public static List<Long> calcObvMa5(List<Long> obvList) {
        List<Long> obvMa5List = new ArrayList<>();
        for (int i = 0; i < obvList.size(); i++) {
            if (i < 4) {
                obvMa5List.add(null);
                continue;
            }
            long sum = 0;
            for (int j = i - 4; j <= i; j++) {
                sum += obvList.get(j);
            }
            obvMa5List.add(sum / 5);
        }
        return obvMa5List;
    }


    /**
     * 计算单只股票最新分时共振信号
     *
     * @param currTech 最新分时指标
     * @param prevTech 前一分钟分时指标
     * @param barMinuteList  按时间正序的分时行情列表
     * @return [共振信号, 共振评分]
     */
    public static Object[] judgeMinuteResonance(StockTechMinute currTech, StockTechMinute prevTech, List<StockKlineMinute> barMinuteList) {
        if (barMinuteList.size() < 10) {
            return new Object[]{RESONANCE_NONE, BigDecimal.ZERO};
        }
        int lastIdx = barMinuteList.size() - 1;
        StockKlineMinute currBar = barMinuteList.get(lastIdx);

        int signal = RESONANCE_NONE;
        int totalBuyRule = 10;  // 分时简化：10条买入规则（日线13条）
        int buyMatch = 0;
        int totalSellRule = 8;  // 分时简化：8条卖出规则（日线11条）
        int sellMatch = 0;

        // ---------------------- 分时买入规则（10条，超短线核心） ----------------------
        // 1. 分时MA3上穿MA5（核心短线趋势）
        if (currTech.getEma5() != null && currTech.getEma10() != null && prevTech.getEma5() != null && prevTech.getEma10() != null
                && currTech.getEma5().compareTo(currTech.getEma10()) > 0 && prevTech.getEma5().compareTo(prevTech.getEma10()) <= 0) {
            buyMatch++;
        }

        // 2. 分时MACD金叉（5,13,1）
        if (currTech.getMacdDif() != null && currTech.getMacdDea() != null && prevTech.getMacdDif() != null && prevTech.getMacdDea() != null
                && currTech.getMacdDif().compareTo(currTech.getMacdDea()) > 0 && prevTech.getMacdDif().compareTo(prevTech.getMacdDea()) <= 0) {
            buyMatch++;
        }

        // 3. 分时RSI9在30~70之间且向上
        if (currTech.getRsi6() != null && prevTech.getRsi6() != null && currTech.getRsi6().compareTo(new BigDecimal(30)) > 0
                && currTech.getRsi6().compareTo(new BigDecimal(70)) < 0 && currTech.getRsi6().compareTo(prevTech.getRsi6()) > 0) {
            buyMatch++;
        }

        // 4. 分时KDJ金叉且J<75
        if (currTech.getKdjK() != null && currTech.getKdjD() != null && currTech.getKdjJ() != null && prevTech.getKdjK() != null && prevTech.getKdjD() != null
                && currTech.getKdjK().compareTo(currTech.getKdjD()) > 0 && prevTech.getKdjK().compareTo(prevTech.getKdjD()) <= 0
                && currTech.getKdjJ().compareTo(new BigDecimal(75)) < 0) {
            buyMatch++;
        }

        // 5. 分时WR6 > 70（低吸区）
        if (currTech.getWr6() != null && currTech.getWr6().compareTo(new BigDecimal(70)) > 0) {
            buyMatch++;
        }

        // 6. 价格站上分时BOLL中轨
        if (currTech.getBollMid() != null && currBar.getPrice().compareTo(currTech.getBollMid()) >= 0) {
            buyMatch++;
        }

        // 7. 分时VMACD金叉（量能同步）
        if (currTech.getVmacdDif() != null && currTech.getVmacdDea() != null && prevTech.getVmacdDif() != null && prevTech.getVmacdDea() != null
                && currTech.getVmacdDif().compareTo(currTech.getVmacdDea()) > 0) {
            buyMatch++;
        }

        // 8. 分时OBV > OBV_MA5（资金流入）
        if (currTech.getObv() != null && currTech.getObvMa5() != null && currTech.getObv() > currTech.getObvMa5()) {
            buyMatch++;
        }

        // 9. 最新价 > 分时MA5（即时趋势向上）
        if (currTech.getEma5() != null && currBar.getPrice().compareTo(currTech.getEma5()) > 0) {
            buyMatch++;
        }

        // 10. 成交量放大（当前分钟成交量>前5分钟均量）
        long avgVol5 = 0;
        long sum = 0;
        for (int i = lastIdx - 4; i <= lastIdx; i++) {
            sum += barMinuteList.get(i).getVolume();
        }
        avgVol5 = sum / 5;
        if (avgVol5 > 0 && currBar.getVolume() > avgVol5 * 1.2) {
            buyMatch++;
        }

        // ---------------------- 分时卖出规则（8条，超短线核心） ----------------------
        // 1. 分时MA5下穿MA10
        if (currTech.getEma5() != null && currTech.getEma10() != null && prevTech.getEma5() != null && prevTech.getEma10() != null
                && currTech.getEma5().compareTo(currTech.getEma10()) < 0 && prevTech.getEma5().compareTo(prevTech.getEma10()) >= 0) {
            sellMatch++;
        }

        // 2. 分时MACD死叉
        if (currTech.getMacdDif() != null && currTech.getMacdDea() != null && currTech.getMacdDif().compareTo(currTech.getMacdDea()) < 0) {
            sellMatch++;
        }

        // 3. 分时RSI9>80 或 KDJ J>90（超买）
        if ((currTech.getRsi6() != null && currTech.getRsi6().compareTo(new BigDecimal(80)) > 0)
                || (currTech.getKdjJ() != null && currTech.getKdjJ().compareTo(new BigDecimal(90)) > 0)) {
            sellMatch++;
        }

        // 4. 分时WR6 < 20（超卖，反向卖出）
        if (currTech.getWr6() != null && currTech.getWr6().compareTo(new BigDecimal(20)) < 0) {
            sellMatch++;
        }

        // 5. 价格跌破分时BOLL中轨
        if (currTech.getBollMid() != null && currBar.getPrice().compareTo(currTech.getBollMid()) < 0) {
            sellMatch++;
        }

        // 6. 分时VMACD死叉（量能萎缩）
        if (currTech.getVmacdDif() != null && currTech.getVmacdDea() != null && currTech.getVmacdDif().compareTo(currTech.getVmacdDea()) < 0) {
            sellMatch++;
        }

        // 7. 分时OBV < OBV_MA5（资金流出）
        if (currTech.getObv() != null && currTech.getObvMa5() != null && currTech.getObv() < currTech.getObvMa5()) {
            sellMatch++;
        }

        // 8. 成交量萎缩（当前分钟成交量<前5分钟均量的80%）
        if (avgVol5 > 0 && currBar.getVolume() < avgVol5 * 0.8) {
            sellMatch++;
        }

        // ---------------------- 分时共振信号判定（超短线规则） ----------------------
        // 计算买入评分
        BigDecimal score = new BigDecimal(buyMatch * 100 / totalBuyRule);
        // 强力买入：≥7条匹配 + 评分≥70（分时更激进）
        if (buyMatch >= 7 && score.compareTo(new BigDecimal(70)) >= 0) {
            signal = RESONANCE_BUY;
        } else if (sellMatch >= 4) {
            // 强力卖出：≥4条匹配（分时止损更果断）
            signal = RESONANCE_SELL;
            score = new BigDecimal(sellMatch * 100 / totalSellRule);
        } else if (buyMatch >= 5 && buyMatch < 7) {
            // 趋势走强：5~6条匹配
            signal = RESONANCE_TREND_UP;
        } else if (sellMatch >= 2) {
            // 趋势走弱：2~3条匹配
            signal = RESONANCE_TREND_DOWN;
            score = new BigDecimal(sellMatch * 100 / totalSellRule);
        }

        return new Object[]{signal, score};
    }
}
