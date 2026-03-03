package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.OrgPartner;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgPartnerMapper extends BaseMapper<OrgPartner> {

    @Select("SELECT a.*, JSON_ARRAYAGG(JSON_OBJECT('deptCode', c.dept_code, 'deptName', c.dept_name)) AS deptList " +
            "FROM org_partner a  LEFT JOIN org_partner_dept b ON a.`partner_code`=b.partner_code " +
            "LEFT JOIN org_dept c ON b.dept_code=c.`dept_code` " +
            "GROUP BY a.partnerCode")
    List<OrgPartner> queryOrgPartnerList();


    @Delete("DELETE FROM org_partner_dept WHERE partner_code=#{partnerCode} AND dept_code=#{deptCode} ")
    int deletePartnerDept(@Param("partnerCode") String partnerCode, @Param("deptCode") String deptCode);

    @Select("SELECT COUNT(1) FROM org_partner_dept WHERE partner_code= #{partnerCode} AND dept_code= #{deptCode} ")
    int countPartnerDept(@Param("partnerCode") String code, @Param("deptCode") String deptCode);

    @Insert("INSERT INTO org_partner_dept(partner_code, dept_code) VALUES (#{partnerCode}, #{deptCode} )")
    int addPartnerDept(@Param("partnerCode") String partnerCode, @Param("deptCode") String deptCode);
}
