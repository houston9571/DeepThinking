package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 股票基本资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_info")
public class StockInfo extends BaseEntity {


    /**
     * 股票代码
     */
    @TableId(value = "stock_code", type = IdType.INPUT)
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 上市交易所
     */
    private String market;

    /**
     * 公司全称
     */
    private String gsmc;

    /**
     * 英文名称
     */
    private String ywmc;

    /**
     * 曾用名
     */
    private String cym;

    /**
     * 区域
     */
    private String qy;

    /**
     * 所属行业
     */
    private String sshy;

    /**
     * 所属证监会行业
     */
    private String sszjhhy;

    /**
     * 成立日期
     */
    private String clrq;

    /**
     * 上市日期
     */
    private String ssrq;


    /**
     * 法人代表
     */
    private String frdb;

    /**
     * 董事长
     */
    private String dsz;

    /**
     * 注册资本
     */
    private String zczb;

    /**
     * 注册地址
     */
    private String zcdz;

    /**
     * 公司简介
     */
    private String gsjj;

    /**
     * 经营范围
     */
    private String jyfw;

    /**
     * 发行量(股)
     */
    private String fxl;

    /**
     * 每股发行价
     */
    private String mgfxj;

    /**
     * 募资资金净额
     */
    private String mjzjje;

    private int sort;


}
