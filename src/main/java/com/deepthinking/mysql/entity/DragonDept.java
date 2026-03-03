package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.core.ext.CountUtilWriter;
import com.deepthinking.core.ext.StringToDateReader;
import lombok.*;

import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("dragon_dept")
public class DragonDept extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 营业部代码
     */
    @JSONField(alternateNames = "OPERATEDEPT_CODE")
    private String deptCode;

    /**
     * 营业部名称
     */
    @JSONField(alternateNames = "ORG_NAME_ABBR")
    private String deptName;

    /**
     * 营业部名称
     */
    @TableField(exist = false)
    @JSONField(alternateNames = "OPERATEDEPT_NAME")
    private String nameFull;


    @TableField("trade_date")
    @JSONField(alternateNames = "ONLIST_DATE", format = "yyyy-MM-dd", deserializeUsing = StringToDateReader.class)
    private LocalDate tradeDate;

    /**
     * 龙虎榜 净买额
     */
    @JSONField(alternateNames = "TOTAL_NETAMT", serializeUsing = CountUtilWriter.class)
    private Long netBuyAmount;
    /**
     * 龙虎榜 买入额
     */
    @JSONField(alternateNames = "TOTAL_BUYAMT", serializeUsing = CountUtilWriter.class)
    private Long buyAmount;

    /**
     * 龙虎榜 卖出额
     */
    @JSONField(alternateNames = "TOTAL_SELLAMT", serializeUsing = CountUtilWriter.class)
    private Long sellAmount;

    @TableField(exist = false)
    @JSONField(alternateNames = "BUY_STOCK")
    private String buyStock;

    @TableField(exist = false)
    @JSONField(alternateNames = "SECURITY_NAME_ABBR")
    private String buyStockName;

    private String buyStocks;


}
