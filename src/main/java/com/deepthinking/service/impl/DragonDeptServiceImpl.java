package com.deepthinking.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.client.EastMoneyDragonApi;
import com.deepthinking.common.thread.Threads;
import com.deepthinking.common.utils.NumberUtils;
import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.DragonDept;
import com.deepthinking.mysql.entity.OrgDept;
import com.deepthinking.mysql.mapper.DragonDeptMapper;
import com.deepthinking.service.DragonDeptService;
import com.deepthinking.service.OrgDeptService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.constant.Constants.LABEL_RESULT;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonDeptServiceImpl extends MybatisBaseServiceImpl<DragonDeptMapper, DragonDept> implements DragonDeptService {

    private final DragonDeptMapper dragonDeptMapper;


    private final EastMoneyDragonApi eastMoneyDragonApi;

    private final OrgDeptService orgDeptService;


    /**
     * 龙虎榜个股营业部列表
     */
    public Result<List<DragonDept>> syncDragonDeptList(String date) {
        int total = 0, pageNum = 0, pageSize = 100;
        Set<OrgDept> orgDeptSet = Sets.newHashSet();
        ArrayList<DragonDept> list = Lists.newArrayList();
        JSONArray data = new JSONArray();
        while (true) {
            ++pageNum;
            try {
                data = syncDragonDeptList(date, pageNum, pageSize);
            } catch (Exception e) {
                try {
                    data = syncDragonDeptList(date, pageNum, pageSize);
                } catch (Exception e1) {
                    Threads.sleep(NumberUtils.random(5000));
                    log.error(">>>>>getDragonDeptList request json error. {}", e.getMessage());
                }
            }
            if (CollectionUtils.isEmpty(data)) {
                break;
            }
            log.info(">>>>>getDragonDeptList {} pageNum:{} data:{}", data, pageNum, data.size());
            total += data.size();
            for (int i = 0; i < data.size(); i++) {
                ++total;
                try {
                    DragonDept d = JSONObject.parseObject(data.getString(i), DragonDept.class);
                    String[] sc = d.getBuyStock().split("\\s+");
                    String[] sn = d.getBuyStockName().split("\\s+");
                    JSONObject stocks = new JSONObject();
                    for (int j = 0; j < sc.length; j++) {
                        stocks.put(sc[j].substring(0, 6), sn[j]);
                    }
                    d.setBuyStocks(stocks.toJSONString());
                    d.setDeptName(d.getDeptName().replace("证券营业部", "").replace("营业部", ""));
                    list.add(d);
                    orgDeptSet.add(OrgDept.builder().deptCode(d.getDeptCode()).deptName(d.getDeptName()).nameFull(d.getNameFull()).build());
                } catch (Exception e) {
                    log.error(">>>>>getDragonDeptList JSONObject.parseObject error. {} {}", data.getString(i), e.getMessage());
                }
            }
            if (data.size() < pageSize) {
                break;
            }
            Threads.sleep(NumberUtils.random(5000));
        }
        log.info(">>>>>getDragonDeptList read finished {} total:{} save:{} ", date, total, list.size());
        try {
            if (!CollectionUtils.isEmpty(list)) {
                saveOrUpdateBatch(list, new String[]{"dept_code", "trade_date"});
                orgDeptService.saveBatch(orgDeptSet);
            }
        } catch (Exception e) {
            log.error(">>>>>getDragonDeptList saveBatch error. {}", e.getMessage());
        }
        return Result.success(list);
    }

    private JSONArray syncDragonDeptList(String date, int pageNum, int pageSize) {
        JSONObject json = eastMoneyDragonApi.syncDragonDeptList(date, pageNum, pageSize);
        JSONObject result = json.getJSONObject(LABEL_RESULT);
        if (ObjectUtil.isEmpty(result) || !result.containsKey(LABEL_DATA)) {
            return null;
        }
        return result.getJSONArray(LABEL_DATA);
    }

}
