package com.deepthinking.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepthinking.client.EastMoneyStockApi;
import com.deepthinking.common.constant.MarketType;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.StockKlineDaily;
import com.deepthinking.mysql.mapper.StockKlineDailyMapper;
import com.deepthinking.service.StockKlineDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.constant.Constants.LABEL_TOTAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockKlineDailyServiceImpl extends MybatisBaseServiceImpl<StockKlineDailyMapper, StockKlineDaily> implements StockKlineDailyService {

    private final StockKlineDailyMapper stockKlineDailyMapper;

    private final EastMoneyStockApi eastMoneyStockApi;


    /**
     * 获取股票实时交易列表，排除688 920 ST
     *
     */
    public List<StockKlineDaily> syncStockKlineDailyList() {
        String fields = "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f14,f15,f16,f17,f18,f20,f21,f23,f24,f34,f35,f37,f40,f41,f45,f46,f48,f49,f57,f64,f65,f66,f69,f70,f71,f72,f75,f76,f77,f78,f81,f82,f83,f84,f87,f109,f129,f297";
        int total = 0, pageNum = 0, pageSize = 100;
        List<StockKlineDaily> list = new ArrayList<>(4500);
        while (true) {
            JSONObject json = eastMoneyStockApi.getStockTradeList(fields, ++pageNum, pageSize, System.currentTimeMillis());
            JSONObject data = json.getJSONObject(LABEL_DATA);
            if (Objects.isNull(data) || !data.containsKey("diff")) {
                break;
            }
            JSONArray array = data.getJSONArray("diff");
            total = data.getInteger(LABEL_TOTAL);
            log.info(">>>>>syncStockTradeList pageNum:{} data:{} total:{}", pageNum, array.size(), total);
            for (int i = 0; i < array.size(); i++) {
                try {
                    StockKlineDaily d = JSONObject.parseObject(array.getString(i), StockKlineDaily.class);
                    if (MarketType.contains(d.getStockCode(), d.getStockName())) {
                        if (ObjectUtil.isNotEmpty(d.getOpen())) {
                            d.setLimitUp(d.getOpen().add(d.getOpen().multiply(MarketType.getChangeLimit(d.getStockCode()))));
                            d.setLimitDown(d.getOpen().subtract(d.getOpen().multiply(MarketType.getChangeLimit(d.getStockCode()))));
                        }
                        d.setMainIn(d.getSuperLargeIn() + d.getLargeIn());
                        d.setMainOut(d.getSuperLargeOut() + d.getLargeOut());
                        d.setMainNetIn(d.getSuperLargeNetIn() + d.getLargeNetIn());
                        d.setMainNetRatio(d.getSuperLargeNetRatio().add(d.getLargeNetRatio()));
                        d.setRetailIn(d.getMediumIn() + d.getSmallIn());
                        d.setRetailOut(d.getMediumOut() + d.getSmallOut());
                        d.setRetailNetIn(d.getMediumNetIn() + d.getSmallNetIn());
                        d.setRetailNetRatio(d.getMediumNetRatio().add(d.getSmallNetRatio()));
                        list.add(d);
                    }
                } catch (Exception e) {
                    log.error(">>>>>syncStockTradeList JSONObject.parseObject error. {} {}", array.getString(i), e.getMessage());
                }
            }
            if (array.size() < pageSize) {
                break;
            }
        }
        try {
            log.info(">>>>>syncStockTradeList read finished total:{} list:{} ", total, list.size());
            if (!CollectionUtils.isEmpty(list)) {
                delete(new LambdaQueryWrapper<StockKlineDaily>().eq(StockKlineDaily::getTradeDate, list.get(0).getTradeDate()));
                saveBatch(list);
            }
        } catch (Exception e) {
            log.error(">>>>>syncStockTradeList saveBatch error. {}", e.getMessage());
        }
        return list;
    }
}

