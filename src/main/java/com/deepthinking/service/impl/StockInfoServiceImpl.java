package com.deepthinking.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepthinking.client.EastMoneyStockApi;
import com.deepthinking.common.utils.StringUtil;
import com.deepthinking.core.base.Result;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.ConceptInfo;
import com.deepthinking.mysql.entity.ConceptStock;
import com.deepthinking.mysql.entity.StockInfo;
import com.deepthinking.mysql.mapper.StockInfoMapper;
import com.deepthinking.service.ConceptInfoService;
import com.deepthinking.service.ConceptStockService;
import com.deepthinking.service.StockInfoService;
import com.deepthinking.sprider.SpriderTemplateParser;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.constant.Constants.LABEL_RESULT;
import static com.deepthinking.common.enums.ErrorCode.*;
import static com.deepthinking.core.components.MarketType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInfoServiceImpl extends MybatisBaseServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {

    private final StockInfoMapper stockInfoMapper;

    private final ConceptInfoService conceptInfoService;

    private final ConceptStockService conceptStockService;

    private final EastMoneyStockApi eastMoneyStockApi;


    @Autowired
    SpriderTemplateParser spiderTemplateParser;

    public Result<Integer> syncStockInfoAll() {
        List<StockInfo> inserts = stockInfoMapper.queryStockInfoNotIn();
        log.error(">>>>>syncStockInfoAll start. 新增：{}", inserts.size());
        for (StockInfo daily : inserts) {
            Result<StockInfo> result = syncStockInfo(daily.getStockCode());
            if (result.isSuccess()) {
                syncStockConceptList(daily.getStockCode());
            }
        }
        log.error(">>>>>syncStockInfoAll end. 新增：{}", inserts.size());

        List<StockInfo> updates = findAll();
        log.error(">>>>>syncStockInfoAll start. 更新：{}", updates.size());
        for (StockInfo daily : updates) {
            Result<StockInfo> result = syncStockInfo(daily.getStockCode());
            if (result.isSuccess()) {
                syncStockConceptList(daily.getStockCode());
            }
        }
        log.error(">>>>>syncStockInfoAll end. 更新：{}", updates.size());
        return Result.success(inserts.size() + updates.size());
    }


    /**
     * 股票基本信息
     */
    public Result<StockInfo> syncStockInfo(String stockCode) {
        try {
            if (exist(new LambdaQueryWrapper<StockInfo>().eq(StockInfo::getStockCode, stockCode).gt(StockInfo::getUpdateTime, LocalDateTime.of(LocalDate.now(), LocalTime.MIN)))) {
                return Result.fail(DATA_UPDATED, "getStockInfo", stockCode);
            }

            String tpl = stockCode.length() == 6 ? "S01-overview.json" : "S01-overview-hk.json";
            if (StringUtil.equals(getMarket(stockCode), MARKET_BJ)) {
                return Result.fail(REQUEST_UNSUPPORTED);
            }
            List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap(tpl, stockCodeMap(stockCode));
            if (CollectionUtils.isEmpty(factors)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "tlp:" + tpl);
            }
            Map<String, String>[] maps = factors.get(0);
            if (ArrayUtils.isEmpty(maps)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "factors:" + JSONObject.toJSONString(factors));
            }

            StockInfo stockInfo = BeanUtil.fillBeanWithMap(maps[0], new StockInfo(), true);
            saveOrUpdate(stockInfo, new String[]{"stock_code"});
            return Result.success(stockInfo);
        } catch (Exception e) {
            log.error(">>>>>getStockInfo error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 个股所属概念
     */
    public Result<Void> syncStockConceptList(String stockCode) {
        try {
            JSONObject json = eastMoneyStockApi.syncStockConcepts(stockCode, getMarket(stockCode));
            JSONObject result = json.getJSONObject(LABEL_RESULT);
            if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockConceptList result is null");
            }
            JSONArray data = result.getJSONArray(LABEL_DATA);
            if (CollectionUtils.isEmpty(data)) {
                return Result.fail(NOT_GET_PAGE_ERROR, "getStockConceptList data is null");
            }
            JSONObject d;
            List<ConceptStock> conceptStockList = Lists.newArrayList();
            for (int i = 0; i < data.size(); i++) {
                d = data.getJSONObject(i);
                String conceptCode = d.getString("NEW_BOARD_CODE");
                if (!conceptInfoService.exist(new LambdaQueryWrapper<ConceptInfo>().eq(ConceptInfo::getConceptCode, conceptCode))) {
                    // 添加不存在的概念名称
                    conceptInfoService.save(ConceptInfo.builder().conceptCode(conceptCode).conceptName(d.getString("BOARD_NAME")).type(d.getString("BOARD_TYPE")).level(d.getString("BOARD_LEVEL")).build());
                }
                conceptStockList.add(ConceptStock.builder().stockCode(stockCode).conceptCode(conceptCode).build());
            }
            conceptStockService.delete(new LambdaQueryWrapper<ConceptStock>().eq(ConceptStock::getStockCode, stockCode));
            conceptStockService.saveBatch(conceptStockList);
        } catch (Exception e) {
            log.error(">>>>>getStockConceptList error. {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }


}
