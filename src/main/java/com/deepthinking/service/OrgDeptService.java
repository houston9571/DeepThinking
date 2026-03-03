package com.deepthinking.service;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.OrgDept;

import java.util.List;
import java.util.Set;

public interface OrgDeptService extends MybatisBaseService<OrgDept> {


    int saveBatch(Set<OrgDept> orgDeptSet);

    Result<List<OrgDept>> queryNomatchPartnerDeptList(String partnerCode);


}
