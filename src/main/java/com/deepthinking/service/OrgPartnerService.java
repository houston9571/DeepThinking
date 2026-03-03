package com.deepthinking.service;

import com.deepthinking.core.base.Result;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.OrgPartner;

import java.util.List;

public interface OrgPartnerService extends MybatisBaseService<OrgPartner> {


    Result<List<OrgPartner>> queryOrgPartnerList();

    Result<Void> deletePartnerDept(String partnerCode, String deptCode);

    Result<Void> addPartnerDept(String partnerCode, String deptCode);



}
