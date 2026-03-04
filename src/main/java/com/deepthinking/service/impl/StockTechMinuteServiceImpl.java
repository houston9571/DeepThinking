package com.deepthinking.service.impl;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.client.EastMoneyStockApi;
import com.deepthinking.common.constant.MarketType;
import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;
import com.deepthinking.mysql.mapper.StockTechMinuteMapper;
import com.deepthinking.service.StockTechMinuteService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.constant.StockConstants.KLINE_1MIN;
import static com.deepthinking.common.enums.ErrorCode.DATA_UNPAIR;
import static com.deepthinking.common.enums.ErrorCode.NOT_GET_PAGE_ERROR;
import static com.deepthinking.service.impl.StockIndicatorMinuteCalculator.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTechMinuteServiceImpl extends MybatisBaseServiceImpl<StockTechMinuteMapper, StockTechMinute> implements StockTechMinuteService {

    private final StockTechMinuteMapper stockTechMinuteMapper;

    private final EastMoneyStockApi eastMoneyStockApi;


    /**
     * 股票每分钟交易行情和资金流向 每1分钟
     * 获取最后10条，容错及指标计算使用
     */
    public Result<Void> syncStockTrendsMinute(String stockCode) {
        JSONObject json = eastMoneyStockApi.getStockTrends(stockCode, MarketType.getMarketCode(stockCode), SystemClock.now());
        JSONObject data = json.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("trends")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "getStockTrends");
        }
        JSONArray trends = data.getJSONArray("trends");

        json = eastMoneyStockApi.getFundsFlowLines(stockCode, MarketType.getMarketCode(stockCode), KLINE_1MIN, 15);
        data = json.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "getFundsFlowLines");
        }
        JSONArray lines = data.getJSONArray("klines");
        String stockName = data.getString("name");

        List<StockTechMinute> techMinuteList = Lists.newArrayList();
        int lastIdx = lines.size() - 1;
        for (int i = trends.size() - lines.size(); i < trends.size(); i++) {
            StockTechMinute tech = StockTechMinute.builder().stockCode(stockCode).stockName(stockName).build();
            String[] trend = trends.getString(i).split(COMMA);
            if (i == lastIdx) {
                String[] lastLine = lines.getString(lastIdx).split(COMMA);
                if (!StrUtil.equals(lastLine[0], trend[0])) {               // 最后一分钟的时间对齐
                    return Result.fail(DATA_UNPAIR, lastLine[0], trend[0]);
                }
                tech.setMainNetIn(lastLine[1]);
                tech.setSmallNetIn(lastLine[2]);
                tech.setMediumNetIn(lastLine[3]);
                tech.setLargeNetIn(lastLine[4]);
                tech.setSuperLargeNetIn(lastLine[5]);
            }
            String[] t = trend[0].split("\\s+");
            tech.setTradeDate(DateUtils.parseLocalDate(t[0], DateFormatEnum.DATE));
            tech.setTradeTime(DateUtils.parseLocalTime(t[1] + ":00", DateFormatEnum.TIME));
            tech.setOpen(new BigDecimal(trend[1]));
            tech.setClose(new BigDecimal(trend[2]));
            tech.setHigh(new BigDecimal(trend[3]));
            tech.setLow(new BigDecimal(trend[4]));      // 这四个价格需要再核对字段
            tech.setVolume(Double.valueOf(trend[5]).longValue());     // 分时成交量
            tech.setAmount(Double.valueOf(trend[6]).longValue());     // 分时成交额
            tech.setTotalVolume(Double.valueOf(trend[10]).longValue());     // 总成交量
            tech.setTotalAmount(Double.valueOf(trend[11]).longValue());     // 总成交额
            techMinuteList.add(tech);
        }
        StockTechMinute tech = Ta4jMinuteIndicatorCalculator.calcMinuteIndicator(techMinuteList);
        saveOrUpdate(tech, new String[]{"stock_code", "trade_date", "trade_time"});
        return Result.success();
    }



    public void calculateMinuteIndicatorAndSave(List<StockKlineMinute> prev10MinuteList) {
        // 至少需要10分钟数据（适配分时MA10/BOLL10）
        if (prev10MinuteList.size()  < 10) {
            log.warn("分时数据不足不计算，必须满足10条");
            return;
        }

        // 读取分时线数据（按时间升序）
        int last = prev10MinuteList.size() - 1;
        // 抽取核心序列（分时简化：高低价用最新价，实际可替换为真实分时高低价）
        List<BigDecimal> prices = new ArrayList<>();
        List<BigDecimal> highs = new ArrayList<>();
        List<BigDecimal> lows = new ArrayList<>();
        List<Long> volumes = new ArrayList<>();
        for (StockKlineMinute m : prev10MinuteList) {
            prices.add(m.getPrice());
            highs.add(m.getHigh());
            lows.add(m.getLow());
            volumes.add(m.getVolume());
        }

        // 1. 计算基础指标
        List<BigDecimal> ma3 = calcMa(prices, 3);
        List<BigDecimal> ma5 = calcMa(prices, 5);
        List<BigDecimal> ma10 = calcMa(prices, 10);
        List<BigDecimal[]> macd = calcMacd(prices);
        List<BigDecimal> rsi6 = calcRsi(prices, 6);
//        List<BigDecimal> rsi9 = calcRsi(prices, 9);
        List<BigDecimal[]> kdj = calcKdj(highs, lows, prices);
        List<BigDecimal> wr6 = calcWr6(highs, lows, prices);
        List<BigDecimal[]> boll = calcBoll(prices);
        List<BigDecimal[]> vmacd = calcVmacd(volumes);
        List<Long> obv = calcObv(prices, volumes);
        List<Long> obvMa5 = calcObvMa5(obv);

        // 2. 组装最新分时指标
        List<StockTechMinute> techList = Lists.newArrayList();
        for (int i = last-1; i < prev10MinuteList.size(); i++) {
            StockKlineMinute bar = prev10MinuteList.get(i);
            StockTechMinute tech = new StockTechMinute();
            tech.setStockCode(bar.getStockCode());
            tech.setStockName(bar.getStockName());
            tech.setTradeDate(bar.getTradeDate());
            tech.setTradeTime(bar.getTradeTime());
            tech.setHigh(bar.getHigh());
            tech.setLow(bar.getLow());
            tech.setOpen(bar.getOpen());
            tech.setClose(bar.getClose());

            tech.setEma3(ma3.get(i));
            tech.setEma5(ma5.get(i));
            tech.setEma10(ma10.get(i));
            tech.setMacdDif(macd.get(i)[0]);
            tech.setMacdDea(macd.get(i)[1]);
            tech.setMacdBar(macd.get(i)[2]);
            tech.setRsi6(rsi6.get(i));
            tech.setKdjK(kdj.get(i)[0]);
            tech.setKdjD(kdj.get(i)[1]);
            tech.setKdjJ(kdj.get(i)[2]);
            tech.setWr6(wr6.get(i));
            tech.setBollMid(boll.get(i)[0]);
            tech.setBollUpper(boll.get(i)[1]);
            tech.setBollLower(boll.get(i)[2]);
            tech.setVmacdDif(vmacd.get(i)[0]);
            tech.setVmacdDea(vmacd.get(i)[1]);
            tech.setObv(obv.get(i));
            tech.setObvMa5(obvMa5.get(i));
            techList.add(tech);
        }

        // 3. 计算分时共振信号
        StockTechMinute tech = techList.get(1);
        Object[] resonanceResult = judgeMinuteResonance(tech, techList.get(0), prev10MinuteList);
//        tech.setResonanceSignal((Integer) resonanceResult[0]);
//        tech.setResonanceScore((BigDecimal) resonanceResult[1]);

        saveOrUpdate(tech, new String[]{"stock_code", "trade_date", "trade_time"});
    }


}

