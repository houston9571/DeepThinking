package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.StockTechDaily;
import org.apache.ibatis.annotations.Select;

public interface StockTechDailyMapper extends BaseMapper<StockTechDaily> {

//   「尾盘选股 + 次日卖出」的关键是：尾盘确认强势（排除日内假涨）、隔夜有溢价、次日早盘快速兑现
//    核心指标	            尾盘选股（2:30 后）阈值	    次日卖出（上午）参考阈值	    核心作用
//    量比（volume_ratio）	≥1.5（尾盘仍放量，非尾盘偷袭）	≤1.0（放量转缩量，溢价消失）	验证上涨的真实性，避免假突破
//    换手率（turnover）	    8%-15%（适中，无高位出货）	    >20%（主力出货，立即卖）	    控盘度判断，8-15% 是主力温和吸筹区间
//    收盘位置百分比	        ≥80%（尾盘收在高位，多头强势）	<50%（开盘回落，弱势）	        确认尾盘买盘力度，预判次日溢价
//    日线共振信号	        1（强力买入）	                2/4（走弱信号）	            定趋势，只选日线强趋势票
//    盈亏比	                	                        ≥3%（止盈）/≤-2%（止损）	    次日快速兑现，不贪多

    /**
     * 隔夜持仓选股
     * 尾盘选股: 用「量比≥1.5 + 换手率 8-15%+ 收盘位置≥80%」筛选强势股，确保上涨真实、无出货风险；
     * 1. 14:30后只选「尾盘强势、隔夜风险低」的票，过滤尾盘偷袭、高换手出货的假强势股
     * 2. 14:50-15:00 下单，避免提前买入被砸
     */
    @Select("SELECT d.stock_code, d.trade_date, d.close, d.volume_ratio, d.turnover, t.resonance_signal, t.resonance_score, t.cost_concentration, " +
            "    ROUND((d.close - d.low) / (d.high - d.low) * 100, 2) AS close_position, " +  // 收盘位置_百分比 >70% 强→敢买、敢拿  30~70% 震荡→不买不卖  <30% 弱→必卖、不恋战
            "    (SELECT AVG(m.volume_ratio) FROM stock_kline_minute m WHERE m.stock_code = d.stock_code  " +
            "       AND m.trade_date = CURDATE() AND m.trade_time BETWEEN '14:00:00' AND '15:00:00') AS avg_volume " +         // 尾盘量能验证（14:00-15:00的分时量比）越高越好
            "FROM stock_kline_daily d LEFT JOIN stock_tech_daily t ON d.stock_code = t.stock_code AND d.trade_date = t.trade_date " +
            "WHERE d.trade_date = CURDATE() " +
            "    AND d.close > 6 " +                            // 剔除低价垃圾股
            "    AND t.cost_concentration < 12 " +              // 筹码集中
            "    AND t.resonance_signal IN (1, 3) " +           // 核心条件1：日线共振信号 = 1 或 3（多头）
            "    AND t.resonance_score >= 80 " +
            "    AND d.volume_ratio >= 1.5 " +                  // 核心条件2：尾盘量能真实
            "    AND d.turnover BETWEEN 8 AND 15 " +            // 核心条件3：换手率 → ＜8%：不够活跃；  8%~15%：主力健康吸筹 → 可以买；  ＞20%：主力疯狂出货 → 必须卖
            "    AND ROUND((d.close - d.low)/(d.high - d.low) * 100, 2) >= 80 " +   // 核心条件4：收盘位置 隔夜>=80
            "    AND (SELECT AVG(m.volume_ratio) FROM stock_kline_minute m WHERE m.stock_code = d.stock_code AND m.trade_date = CURDATE() AND m.trade_time BETWEEN '14:00:00' AND '15:00:00') >= 1.2 " +
            "    AND (d.close - d.open)/d.open < 0.05 " +       // 排除高开过多（避免次日回调）
            "ORDER BY close_position DESC, avg_volume DESC, t.cost_concentration ASC " +
            "LIMIT 10 ")
    void queryOvernightBuy();

    /**
     * 隔夜持仓卖出
     * 卖出核心： 次日上午用「浮亏≥2% 止损、浮盈≥3% 止盈、11:00 前清仓」的规则，快速兑现，不博弈午后；
     * 1. 9:30 开盘后，每5分钟运行一次卖出 SQL，监控触发条件；10:00左右卖出
     * 2. 触发止损 / 止盈 / 量能走弱→立即执行卖出；
     * 3. 11:00 前无论盈亏，卖出剩余持仓（隔夜策略不持有过午）。
     */
    @Select("SELECT p.stock_code, p.buy_price, m.price, m.volume_ratio, d.turnover, " +
            "    ROUND((m.price - p.buy)/p.buy * 100, 2) AS profit, " +
            "    ROUND((m.price - m.low)/(m.high - m.low) * 100, 2) AS close_position, " +  // 收盘位置_百分比 >70% 强→敢买、敢拿  30~70% 震荡→不买不卖  <30% 弱→必卖、不恋战
            "    CASE WHEN (m.price - p.buy_price)/p.buy_price <= -0.02 THEN '【最高优先级】止损≥2% → 立即卖出（隔夜容错低）' " +
            "        WHEN d.turnover > 20 THEN '【高优先级】换手率>20% → 立即卖出（主力出货）' " +
            "        WHEN (m.price - p.buy_price)/p.buy_price >= 0.03 THEN '【中优先级】止盈≥3% → 分批卖出（落袋为安）' " +
            "        WHEN m.volume_ratio < 1 AND ROUND((m.price - m.low)/(m.high - m.low) * 100, 2) < 50 THEN '【中优先级】量比≤1.0 → 全部卖出（量能走弱）' " +
            "        ELSE '【最低优先级】11:00前无条件清仓（隔夜不持有过午）' " +
            "    END AS sell_reason " +    // 卖出建议
            "FROM stock_position p " +
            // 关联次日早盘最新分时数据（窗口函数取最新一条，避免多次嵌套）
            "   LEFT JOIN ( SELECT stock_code, price, volume_ratio FROM stock_kline_minute " +
            "      WHERE trade_date = DATE_ADD(p.buy_date, INTERVAL 1 DAY)  AND trade_time BETWEEN '09:30:00' AND '11:30:00' " + // 精准匹配买入次日 限定早盘时间范围
            "      QUALIFY ROW_NUMBER() OVER (PARTITION BY stock_code ORDER BY trade_time DESC) = 1 " +
            "   ) m ON p.stock_code = m.stock_code " +
            // 关联次日日线数据（合并查询，减少IO）
            "   LEFT JOIN ( SELECT stock_code, turnover, high, low FROM stock_kline_daily WHERE trade_date = DATE_ADD(p.buy_date, INTERVAL 1 DAY) ) d ON p.stock_code = d.stock_code" +
            "WHERE p.status = 1 AND p.strategy = 1 " +      //  1=隔夜持仓 2=日内短线
            "ORDER BY " +
            "    CASE WHEN (m.price - p.buy_price)/p.buy_price <= -0.02 THEN 1 " +
            "        WHEN d.turnover > 20 THEN 2 " +
            "        WHEN (m.price - p.buy_price)/p.buy_price >= 0.03 THEN 3 " +
            "        WHEN m.volume_ratio <= 1 THEN 4 " +
            "        ELSE 5 " +
            "    END ASC ")
    void queryOvernightSell();


    /**
     * 日内短线（1-3天）买入
     * 超短线共振买入预警（连续2日验证）
     * 核心
     * 只监控9:30-14:30（超短线最佳开仓窗口）；
     * 强制验证MA3/MA5 多头 + MACD/VMACD 双金叉 + 量能放大（过滤假信号）；
     * 精准版叠加筹码集中度 < 15%+ 共振评分≥70（只选主力控盘的票）
     * <p>
     * 执行： 5 分钟跑一次
     */
    @Select("SELECT d.stock_code, d.trade_date, d.close, t.resonance_signal, t.resonance_score, d.volume_ratio, d.turnover, t.cost_concentration, " +
            "    ROUND((d.close - d.low) / (d.high - d.low) * 100, 2) AS close_position, " +  // 收盘位置_百分比 >70% 强→敢买、敢拿  30~70% 震荡→不买不卖  <30% 弱→必卖、不恋战
            "     d.close_price > t.ma10 AS cross_ma10, " +         // 是否站在MA10之上
            "   CASE WHEN t.resonance_signal = 1 AND t.resonance_score >= 80 THEN '强力买入（核心）' " +
            "        WHEN t.resonance_signal = 1 AND t.resonance_score >= 70 THEN '强力买入（备选）' " +
            "        WHEN t.resonance_signal = 3 AND t.resonance_score >= 75 THEN '趋势向上（观察）' " +
            "        ELSE '无信号' " +
            "    END AS resonance, " +      // 共振类型
            "    (SELECT COUNT(1) FROM stock_tech_daily t2  " +
            "     WHERE t2.stock_code = d.stock_code  " +
            "     AND t2.trade_date >= DATE_SUB(d.trade_date, INTERVAL 1 DAY)  " +
            "     AND t2.trade_date <= d.trade_date  " +
            "     AND t2.resonance_signal IN (1,3)  " +
            "     AND t2.resonance_score >= 70) AS resonance_day " +    // 连续共振天数（验证趋势）
            "FROM stock_kline_daily d LEFT JOIN stock_tech_daily t ON d.stock_code = t.stock_code AND d.trade_date = t.trade_date  " +
            "WHERE d.trade_date = CURDATE()" +
            "    AND d.close > 6 " +                        // 剔除低价股
            "    AND t.cost_concentration < 12 " +          // 筹码高度集中
            "    AND t.resonance_signal IN (1, 3)  " +      // 核心条件1：日线共振信号有效
            "    AND t.resonance_score >= 70 " +
            "    AND d.volume_ratio > 1.2 " +               // 核心条件2：量能有效（直接用日线量比>1.2）
            "    AND d.turnover < 15 " +                    // 换手率（<15%）
            "    AND ROUND((d.close - d.low)/(d.high - d.low)*100,2) > 70" + // 收盘位置 > 70% + 日线共振买入信号 + 量比 > 1.2  策略：次日 高开/回踩不破5日线 可加仓或持股
            "    AND d.close > t.ma10 " +                   // 收盘价>MA10
            "    AND EXISTS ( " +                           // 核心条件3：连续2日共振（过滤单日假信号）
            "        SELECT 1 FROM stock_tech_daily t2 " +
            "        WHERE t2.stock_code = d.stock_code " +
            "        AND t2.trade_date = DATE_SUB(d.trade_date, INTERVAL 1 DAY) " +
            "        AND t2.resonance_signal IN (1, 3) " +
            "        AND t2.resonance_score >= 65 " +
            "    ) " +
            "ORDER BY " +
            "   CASE WHEN t.resonance_signal = 1 AND t.resonance_score >= 80 THEN 1 " +
            "        WHEN t.resonance_signal = 1 AND t.resonance_score >= 70 THEN 2 " +
            "        WHEN t.resonance_signal = 3 AND t.resonance_score >= 75 THEN 3 " +
            "        ELSE 4 " +
            "    END ASC, " +
            "    t.resonance_score DESC, " +
            "    d.volume_ratio DESC, " +                   // 日线量比越大越优先
            "    t.cost_concentration ASC " +
            "LIMIT 10 ")
    void queryStocksByTechToBuy();

    /**
     * 日内短线（1-3天）卖出
     * 超短线共振卖出信号：
     * 优先级：止损（亏损≥3%）> 止盈（盈利≥5%）> 顶背离 > 共振卖出 > 量能萎缩；
     * 严格执行超短风控：亏损≥3% 无条件止损，盈利≥5% 优先止盈；
     * 提前预判趋势：MA3 下穿 MA5、量能萎缩时直接卖出，不扛单。
     *
     * 执行：30 分钟跑一次，收盘前 10 分钟必跑一次。
     */
    @Select("SELECT p.stock_code, p.buy_price, d.close, d.volume_ratio, d.turnover, t.resonance_signal, t.resonance_score, t.cost_concentration, " +
            "     t.ma3 <= t.ma5 AS under_m3_ma5, " +         // 是否 MA3 下穿 MA5
            "    ROUND((d.close - p.buy_price) / p.buy_price * 100, 2) AS profit, " +
            "    ROUND((d.close - d.low) / (d.high - d.low) * 100, 2) AS close_position " +  // 收盘位置_百分比 弱势区：< 30%
            "   CASE WHEN (d.close - p.buy_price) / p.buy_price <= -0.03 THEN '【最高优先级】止损≥3% → 立即卖出' " +
            "        WHEN t.divergence_type = 1 AND d.volume_ratio < 0.8 THEN '【高优先级】顶背离,量比<0.8 → 立即卖出（量能萎缩）' " +
            "        WHEN ROUND((m.price - d.low_price) / (d.high_price - d.low_price) * 100, 2) < 30 THEN '【高优先级】收盘位置<30% → 立即卖出（弱势）' " +
            "        WHEN (d.close  - p.buy_price) / p.buy_price >= 0.06 THEN '【中优先级】止盈≥6% → 立即卖出（落袋为安）' " +
            "        WHEN t.resonance_signal = 2 THEN '【中优先级】共振信号强力卖出 → 立即卖出（趋势反转） ' " +
            "        WHEN t.resonance_signal = 4 AND EXISTS ( " +
            "            SELECT 1 FROM stock_tech_daily t2 WHERE t2.stock_code = d.stock_code  " +
            "            AND t2.trade_date = DATE_SUB(d.trade_date, INTERVAL 1 DAY) AND t2.resonance_signal = 4 " +
            "        ) THEN '【中优先级】连续2日趋势走弱 → 立即卖出（趋势反转）' " +
            "        WHEN d.turnover > 20 THEN '【中优先级】高换手率风险（换手率>20%，主力出货）' " +
            "        ELSE '【最低优先级】14:30前无条件清仓（日内不隔夜）' " +
            "    END AS sell_reason, " +
            "FROM stock_position p LEFT JOIN stock_kline_daily d ON p.stock_code = d.stock_code AND d.trade_date = CURDATE() " +
            "    LEFT JOIN stock_tech_daily t ON d.stock_code = t.stock_code AND d.trade_date = t.trade_date " +
            "WHERE p.status = 1 AND p.strategy = 2 " +      //  1=隔夜持仓 2=日内短线
//            "    AND ( " +
//            "        (d.close - p.buy_price) / p.buy_price <= -0.03 " +
//            "        OR (d.close - p.buy_price) / p.buy_price >= 0.06 " +
//            "        OR (t.divergence_type = 1 AND d.volume_ratio < 0.8) " +
//            "        OR t.resonance_signal = 2 " +
//            "        OR (t.resonance_signal = 4 AND EXISTS ( " +
//            "            SELECT 1 FROM stock_tech_daily t2  " +
//            "            WHERE t2.stock_code = d.stock_code AND t2.resonance_signal = 4  " +
//            "            AND t2.trade_date = DATE_SUB(d.trade_date, INTERVAL 1 DAY) )" +  // 前一天signal=4，连续2天趋势走弱
//            "        ) OR d.turnover > 20 " +                 // 高换手率卖出条件
//            "    ) " +
//            "    AND ROUND((d.close - d.low)/(d.high - d.low) * 100, 2) < 30 " +   // 收盘位置 < 30% + 量比 < 0.8（量缩）或 共振走弱, 策略：次日无条件减仓/清仓，不博弈反包
            "ORDER BY " +
            "   CASE WHEN (d.close - p.buy_price) / p.buy_price <= -0.03 THEN 1 " +
            "        WHEN d.turnover > 20 THEN 2 " +        //  高换手率风险次之
            "        WHEN (d.close - p.buy_price) / p.buy_price >= 0.06 THEN 3 " +
            "        WHEN (t.divergence_type = 1 AND d.volume_ratio < 0.8) THEN 4 " +
            "        WHEN t.resonance_signal IN (2, 4) THEN 5 " +
            "        ELSE 6 " +
            "    END ASC ")
    void queryStocksByTechToSell();
}
