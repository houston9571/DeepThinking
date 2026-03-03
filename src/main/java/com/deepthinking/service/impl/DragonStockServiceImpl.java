package com.deepthinking.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepthinking.client.EastMoneyDragonApi;
import com.deepthinking.common.thread.Threads;
import com.deepthinking.common.utils.NumberUtils;
import com.deepthinking.core.base.Result;
import com.deepthinking.core.components.MarketType;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.DragonStock;
import com.deepthinking.mysql.entity.StockKlineDaily;
import com.deepthinking.mysql.mapper.DragonStockMapper;
import com.deepthinking.mysql.vo.DragonDetailPartner;
import com.deepthinking.mysql.vo.DragonDetailStockKline;
import com.deepthinking.service.DragonStockService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.deepthinking.common.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonStockServiceImpl extends MybatisBaseServiceImpl<DragonStockMapper, DragonStock> implements DragonStockService {

    private final DragonStockMapper dragonStockMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    public List<DragonDetailStockKline> queryDragonStockList(String tradeDate) {
        return dragonStockMapper.queryDragonStockList(tradeDate);
    }

    public List<DragonDetailStockKline> queryDragonStockDetail(String stockCode) {
        List<DragonDetailStockKline> list = dragonStockMapper.queryDragonStockDetail(stockCode);
        // 将游资按个股合并为一条记录
        Map<String, DragonDetailStockKline> map = Maps.newLinkedHashMap();
        for (DragonDetailStockKline stock : list) {
            String k = stock.getTradeDate().toString();
            DragonDetailStockKline s = new DragonDetailStockKline();
            BeanUtils.copyProperties(stock, s);
            if (map.containsKey(k)) {
                map.get(k).getPartners().add(s);
            } else {
                stock.setPartners(new ArrayList<DragonDetailStockKline>() {{
                    add(s);
                }});
                map.put(k, stock);
            }
        }
        return new ArrayList<>(map.values());
    }


    public List<DragonDetailPartner> queryDragonPartnerDetail(String partnerCode) {
        List<DragonDetailStockKline> list = dragonStockMapper.queryDragonPartnerDetail(partnerCode);
        // 将游资按个股合并为一条记录
        Map<String, DragonDetailPartner> map = Maps.newLinkedHashMap();
        for (DragonDetailStockKline stock : list) {
            String k = stock.getTradeDate().toString();
            StockKlineDaily stockKlineDaily = StockKlineDaily.builder().build();
            BeanUtils.copyProperties(stock, stockKlineDaily);
            if (map.containsKey(k)) {
                map.get(k).getStocks().add(stockKlineDaily);
            } else {
                DragonDetailPartner partner = DragonDetailPartner.builder().build();
                BeanUtils.copyProperties(stock, partner);
                partner.setStocks(new ArrayList<StockKlineDaily>() {{
                    add(stockKlineDaily);
                }});
                map.put(k, partner);
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 龙虎榜个股列表
     */
    public Result<List<DragonStock>> syncDragonStockList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Map<String, DragonStock> map = Maps.newHashMap();
        JSONArray data = new JSONArray();
        while (true) {
            ++pageNum;
            try {
                data = syncDragonStockList(date, pageNum, pageSize);
            } catch (Exception e) {
                try {
                    data = syncDragonStockList(date, pageNum, pageSize);
                } catch (Exception e1) {
                    Threads.sleep(NumberUtils.random(5000));
                    log.error(">>>>>getDragonStockList request json error. {}", e.getMessage());
                }
            }
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            log.info(">>>>>getDragonStockList {} pageNum:{} data:{}", data, pageNum, data.size());
            total += data.size();
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    DragonStock d = JSONObject.parseObject(data.getString(i), DragonStock.class);
                    if (MarketType.contains(d.getStockCode(), d.getStockName())) {
                        d.setBuyAmountRatio(BigDecimal.valueOf(d.getBuyAmount()).divide(BigDecimal.valueOf(d.getAmount()), new MathContext(4, ROUND_MODE)).multiply(HUNDRED));
                        d.setSellAmountRatio(BigDecimal.valueOf(d.getSellAmount()).divide(BigDecimal.valueOf(d.getAmount()), new MathContext(4, ROUND_MODE)).multiply(HUNDRED));
                        if (!map.containsKey(d.getStockCode())) {
                            map.put(d.getStockCode(), d);
                        } else {
                            DragonStock o = map.get(d.getStockCode());
                            if (o.getAmount() >= d.getAmount()) {
                                o.setExplains(d.getExplains() + " | " + o.getExplains());
                                o.setExplanation(d.getExplanation() + " | " + o.getExplanation());
                            } else {
                                d.setExplains(o.getExplains() + " | " + d.getExplains());
                                d.setExplanation(o.getExplanation() + " | " + d.getExplanation());
                                map.put(d.getStockCode(), d);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(">>>>>getDragonStockList JSONObject.parseObject error. {} {}", data.getString(i), e.getMessage());
                }
            }
            if (data.size() < pageSize) {
                break;
            }
        }
        log.info(">>>>>getDragonStockList read finished {} total:{} save:{} ", date, total, map.size());
        ArrayList<DragonStock> list = new ArrayList<>(map.values());
        try {
            if (!CollectionUtils.isEmpty(map)) {
                delete(new LambdaQueryWrapper<DragonStock>().eq(DragonStock::getTradeDate, date));
                saveBatch(list);
            }
        } catch (Exception e) {
            log.error(">>>>>getDragonStockList saveBatch error. {}", e.getMessage());
        }
        return Result.success(list);
    }

    private JSONArray syncDragonStockList(String date, int pageNum, int pageSize) {
        JSONObject json = eastMoneyDragonApi.syncDragonStockList(date, pageNum, pageSize);
        JSONObject result = json.getJSONObject(LABEL_RESULT);
        if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
            return null;
        }
        return result.getJSONArray(LABEL_DATA);
    }


}
