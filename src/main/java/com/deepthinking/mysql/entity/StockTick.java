package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 股票分时盘口表（超短线）
 * 对应数据库表：stock_tick
 *
 * @author 量化系统
 * @date 2026-02-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "stock_tick") // MyBatis-Plus表名注解，若无使用可删除
public class StockTick extends BaseEntity {


    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO) // 主键自增，适配MySQL AUTO_INCREMENT
    private Long id;

    /**
     * 股票代码
     */
    @TableField(value = "stock_code")
    private String stockCode;

    /**
     * 交易日期
     */
    @TableField(value = "trade_date")
    private LocalDate tradeDate;

    /**
     * 分时时间戳（精确到分钟）
     */
    @TableField(value = "trade_datetime")
    private LocalDateTime tradeDatetime;

    /**
     * 当前价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 分时均价线（黄线）
     */
    @TableField(value = "avg_price")
    private BigDecimal avgPrice;

    /**
     * 股价是否站均价线上：1=是,0=否
     */
    @TableField(value = "price_vs_avg")
    private Integer priceVsAvg;

    /**
     * 是否涨停：1=是,0=否
     */
    @TableField(value = "is_limit_up")
    private Integer isLimitUp;

    /**
     * 涨停封单金额（万元）
     */
    @TableField(value = "limit_up_order")
    private Integer limitUpOrder;

    /**
     * 封单额/流通市值（%）
     */
    @TableField(value = "limit_up_ratio")
    private BigDecimal limitUpRatio;

    /**
     * 是否炸板：1=是,0=否
     */
    @TableField(value = "explode_board")
    private Integer explodeBoard;

    /**
     * 炸板后是否回封：1=是,0=否
     */
    @TableField(value = "recover_board")
    private Integer recoverBoard;

    /**
     * 买盘
     */
    @TableField(value = "buy_volume")
    private Long buyVolume;

    /**
     * 卖盘
     */
    @TableField(value = "sell_volume")
    private Long sellVolume;

    /**
     * 委比（%）
     */
    @TableField(value = "commission_ratio")
    private BigDecimal commissionRatio;

    /**
     * 委差（手）
     */
    @TableField(value = "commission_diff")
    private Integer commissionDiff;

}