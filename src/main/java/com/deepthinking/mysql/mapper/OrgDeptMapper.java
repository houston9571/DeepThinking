package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.OrgDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgDeptMapper extends BaseMapper<OrgDept> {

    @Select("SELECT *  FROM org_dept WHERE dept_code NOT IN ( SELECT dept_code FROM org_partner_dept WHERE  partner_code = #{partnerCode} )")
    List<OrgDept> queryNomatchPartnerDeptList(@Param("partnerCode") String partnerCode);
}
