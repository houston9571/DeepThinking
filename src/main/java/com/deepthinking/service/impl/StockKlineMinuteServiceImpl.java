package com.deepthinking.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.client.EastMoneyH5Api;
import com.deepthinking.client.EastMoneyStockApi;
import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.utils.StringUtil;
import com.deepthinking.ext.base.Result;
import com.deepthinking.common.constant.MarketType;
import com.deepthinking.common.constant.StockCodeUtils;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockPool;
import com.deepthinking.mysql.entity.StockTechMinute;
import com.deepthinking.mysql.mapper.StockKlineMinuteMapper;
import com.deepthinking.service.StockKlineMinuteService;
import com.deepthinking.service.StockPoolService;
import com.deepthinking.service.StockTechMinuteService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.enums.ErrorCode.DATA_UNPAIR;
import static com.deepthinking.common.enums.ErrorCode.NOT_GET_PAGE_ERROR;
import static com.deepthinking.common.constant.StockConstants.KLINE_1MIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockKlineMinuteServiceImpl extends MybatisBaseServiceImpl<StockKlineMinuteMapper, StockKlineMinute> implements StockKlineMinuteService {

    private final StockKlineMinuteMapper stockKlineMinuteMapper;

    private final EastMoneyStockApi eastMoneyStockApi;

    private final EastMoneyH5Api eastMoneyH5Api;



    /**
     * 股票实时交易行情和资金流向 每1分钟
     * 获取最后10条，容错及指标计算使用
     */
    public Result<Void> syncStockKlineMinute(String stockCode) {
        JSONObject kline = eastMoneyStockApi.getStockTradeRealtime(stockCode, MarketType.getMarketCode(stockCode), SystemClock.now());
        StockKlineMinute stockKlineMinute = JSONObject.parseObject(kline.getString(LABEL_DATA), StockKlineMinute.class);
//        String transactionDate = kline.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(1).getString("e");

        JSONObject flow = eastMoneyStockApi.getFundsFlowLines(stockCode, MarketType.getMarketCode(stockCode), KLINE_1MIN, 240);
        JSONObject data = flow.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "");
        }

        JSONArray lines = data.getJSONArray("klines");
        if (CollectionUtil.isEmpty(lines)) {
            return Result.fail("syncStockKlineMinute 没有数据，可能停盘");
        }
        for (int i = 0; i < lines.size(); i++) {
            String[] line = lines.getString(0).split(COMMA);
            String[] t = line[0].split("\\s+");
            stockKlineMinute.setTradeDate(DateUtils.parseLocalDate(t[0], DateFormatEnum.DATE));
            stockKlineMinute.setTradeTime(DateUtils.parseLocalTime(t[1] + ":00", DateFormatEnum.TIME));
            stockKlineMinute.setMainNetIn(line[1]);
            stockKlineMinute.setSmallNetIn(line[2]);
            stockKlineMinute.setMediumNetIn(line[3]);
            stockKlineMinute.setLargeNetIn(line[4]);
            stockKlineMinute.setSuperLargeNetIn(line[5]);
        }
        saveOrUpdate(stockKlineMinute, new String[]{"stock_code", "trade_date", "trade_time"});
        return Result.success( );
    }


    public Result<JSONObject> getFirstRequest2Data(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("fc", StockCodeUtils.buildSecId(code));
        eastMoneyH5Api.getFirstRequest2Data(params);
        return Result.success();
    }

}
