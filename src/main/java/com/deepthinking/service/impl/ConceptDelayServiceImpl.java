package com.deepthinking.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.client.EastMoneyConceptApi;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.constant.MarketType;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.ConceptDelay;
import com.deepthinking.mysql.mapper.ConceptDelayMapper;
import com.deepthinking.service.ConceptDelayService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.deepthinking.common.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptDelayServiceImpl extends MybatisBaseServiceImpl<ConceptDelayMapper, ConceptDelay> implements ConceptDelayService {

    private final ConceptDelayMapper conceptDelayMapper;

    private final EastMoneyConceptApi eastMoneyConceptApi;

    /**
     * 概念板块查询，竖型列表，第一行表头日期
     */
    public List<List<ConceptDelay>> queryConceptTradeList(int days, int top) {
        List<ConceptDelay> title = conceptDelayMapper.queryConceptTradeDate(days);
        Map<LocalDate, List<ConceptDelay>> map = Maps.newLinkedHashMap();
        for (ConceptDelay conceptDelay : title) {
            conceptDelay.setWeek(DateUtils.getShortWeekName(conceptDelay.getTradeDate()));
            map.put(conceptDelay.getTradeDate(), conceptDelayMapper.queryConceptTop(conceptDelay.getTradeDate(), top));
        }

        List<List<ConceptDelay>> grid = Lists.newArrayList();
        grid.add(title);
        for (int j = 0; j < top; j++) {
            List<ConceptDelay> data = Lists.newArrayList();
            for (ConceptDelay conceptDelay : title) {
                data.add(map.get(conceptDelay.getTradeDate()).get(j));
            }
            grid.add(data);
        }
        return grid;
    }

    /**
     * 概念板块列表，按涨跌幅排序
     * all=false,只更新涨幅前tops的板块
     */
    public void syncConceptTradeList(boolean all, int top) {
        int total = 0, pageNum = 0, pageSize = all ? 100 : top;
        List<ConceptDelay> list = new ArrayList<>();
        do {
            JSONObject json = eastMoneyConceptApi.syncConceptTradeList(++pageNum, pageSize, System.currentTimeMillis());
            JSONObject data = json.getJSONObject(LABEL_DATA);
            if (Objects.isNull(data) || !data.containsKey("diff")) {
                break;
            }
            JSONArray array = data.getJSONArray("diff");
            total = data.getInteger(LABEL_TOTAL);
            log.info(">>>>>getConceptTradeList pageNum:{} data:{} total:{}", pageNum, array.size(), total);
            for (int i = 0; i < array.size(); i++) {
                try {
                    ConceptDelay conceptDelay = JSONObject.parseObject(array.getString(i), ConceptDelay.class);
                    syncConceptFundsFlow(conceptDelay);
                    saveOrUpdate(conceptDelay, new String[]{"concept_code", "trade_date"});
                    list.add(conceptDelay);
                } catch (Exception e) {
                    log.error(">>>>>syncConceptTradeList JSONObject.parseObject error. {} {}", array.getString(i), e.getMessage());
                }
            }
            if (array.size() < pageSize) {
                break;
            }
        } while (all);
        log.info(">>>>>syncConceptTradeList finished total:{} list:{} ", total, list.size());
    }

    /**
     * 获取最新资金流向
     */
    private void syncConceptFundsFlow(ConceptDelay conceptDelay) {
        try {
            JSONObject json = eastMoneyConceptApi.syncFundsFlow(conceptDelay.getConceptCode(), MarketType.getMarketCode(conceptDelay.getConceptCode()), System.currentTimeMillis());
            ConceptDelay d = JSONObject.parseObject(json.getString(LABEL_DATA), ConceptDelay.class);
            BigDecimal acc = BigDecimal.valueOf(conceptDelay.getAmount());
            MathContext mc = new MathContext(4, ROUND_MODE);
            d.setSuperLargeNetRatio(BigDecimal.valueOf(d.getSuperLargeNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setLargeNetRatio(BigDecimal.valueOf(d.getLargeNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setMediumNetRatio(BigDecimal.valueOf(d.getMediumNetIn()).divide(acc, mc).multiply(HUNDRED));
            d.setSmallNetRatio(BigDecimal.valueOf(d.getSmallNetIn()).divide(acc, mc).multiply(HUNDRED));

            d.setMainIn(d.getSuperLargeIn() + d.getLargeIn());
            d.setMainOut(d.getSuperLargeOut() + d.getLargeOut());
            d.setMainNetIn(d.getSuperLargeNetIn() + d.getLargeNetIn());
            d.setMainNetRatio(d.getSuperLargeNetRatio().add(d.getLargeNetRatio()));

            d.setRetailIn(d.getMediumIn() + d.getSmallIn());
            d.setRetailOut(d.getMediumOut() + d.getSmallOut());
            d.setRetailNetIn(d.getMediumNetIn() + d.getSmallNetIn());
            d.setRetailNetRatio(d.getMediumNetRatio().add(d.getSmallNetRatio()));
            BeanUtil.copyProperties(d, conceptDelay, CopyOptions.create().setIgnoreNullValue(true));
        } catch (Exception e) {
            log.error(">>>>>syncConceptFundsFlow {} {} {}", conceptDelay.getConceptCode(), conceptDelay.getConceptName(), e.getMessage());
        }
    }
}

