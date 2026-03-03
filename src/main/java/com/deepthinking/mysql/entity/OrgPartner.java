package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("org_partner")
public class OrgPartner extends BaseEntity {


    @TableId(value = "partner_code", type = IdType.INPUT)
    private String partnerCode;

    private String partnerName;


    /**
     * 一年上榜次数
     */
    private Integer count;

    /**
     * 一年成交额
     */
    private Integer amount;

    /**
     * 最优持仓期
     */
    private Integer holdingPeriod;

    /**
     * 上涨概率
     */
    private BigDecimal rising;

    private String style;

    private String level;

    private String fundSize;

    private String remark;

    private Integer order;

    @TableField(exist = false)
    private String deptList;

    @TableField(exist = false)
    private JSONArray deptArray;

    public JSONArray getDeptArray(){
        return JSONArray.parseArray(deptList);
    }
}
