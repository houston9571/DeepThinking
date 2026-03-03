package com.deepthinking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deepthinking.core.base.Result;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.OrgDept;
import com.deepthinking.mysql.mapper.OrgDeptMapper;
import com.deepthinking.service.OrgDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgDeptServiceImpl extends MybatisBaseServiceImpl<OrgDeptMapper, OrgDept> implements OrgDeptService {

    private final OrgDeptMapper orgDeptMapper;

    /**
     *
     */
    public int saveBatch(Set<OrgDept> orgDeptSet) {
        int count = 0;
        for (OrgDept o : orgDeptSet) {
            if (!exist(new LambdaQueryWrapper<OrgDept>().eq(OrgDept::getDeptCode, o.getDeptCode()))) {
                save(o);
                count++;
            }
        }
        log.info("saveBatch OrgSalesDept total:{} save:{}", orgDeptSet.size(), count);
        return count;
    }

    public Result<List<OrgDept>> queryNomatchPartnerDeptList(String partnerCode) {
        return Result.success(orgDeptMapper.queryNomatchPartnerDeptList(partnerCode));
    }

}
