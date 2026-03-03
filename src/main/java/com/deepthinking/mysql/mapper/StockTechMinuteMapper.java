package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.StockTechMinute;
import org.apache.ibatis.annotations.Select;

public interface StockTechMinuteMapper extends BaseMapper<StockTechMinute> {

    /**
     * 最新分时数据视图
     * CREATE OR REPLACE VIEW v_stock_kline_minute_latest AS
     * SELECT
     *     m.stock_code,
     *     m.trade_date,
     *     m.trade_time,
     *     m.price,
     *     m.open,
     *     m.close,
     *     m.volume,
     *     m.volume_ratio
     * FROM stock_kline_minute m
     * WHERE
     *     m.trade_date = CURDATE()
     *     AND m.trade_time BETWEEN '09:30:00' AND '15:00:00'
     *     AND m.trade_time = (SELECT MAX(trade_time) FROM stock_kline_minute WHERE stock_code = m.stock_code AND trade_date = CURDATE());
     *
     */

    /**
     * 超短线双重共振买入 (优化为5分钟持续验证)
     * 买入核心：只做「日线高分共振 + 分时高分共振 + 放量 + 筹码集中」的票，只选 “双重强力买入” 的股票
     */
    @Select("SELECT m.stock_code, m.close, m.price,t.resonance_score AS daily_score, tm.resonance_score AS minute_score, m.volume_ratio, " +
            "   CASE WHEN t.resonance_signal = 1 AND tm.resonance_signal = 1 THEN '双重强力买入（核心）' " +
            "        WHEN t.resonance_signal = 1 AND tm.resonance_signal = 3 THEN '日线强+分时趋势向上（备选）' " +
            "        ELSE '弱共振（过滤）' " +
            "    END AS resonance, " +                      // 共振类型
            "    t.cost_concentration, " +
            "    TIMESTAMPDIFF(MINUTE,  " +
            "        (SELECT MIN(trade_time) FROM stock_kline_minute m2  " +
            "         WHERE m2.stock_code = m.stock_code AND m2.trade_date = CURDATE()  " +
            "         AND (t.resonance_signal IN (1,3) AND tm.resonance_signal IN (1,3) AND m2.volume_ratio > 1.5)" +
            "        ), m.trade_time) AS keep_minutes " +    // 信号持续时长(分钟)
            "FROM v_stock_kline_minute_latest m " +
            "    LEFT JOIN stock_tech_daily t ON m.stock_code = t.stock_code AND m.trade_date = t.trade_date " +
            "    LEFT JOIN stock_tech_minute tm ON m.stock_code = tm.stock_code AND m.trade_date = tm.trade_date AND m.trade_time = tm.trade_time " +
            "WHERE t.resonance_signal IN (1, 3) " +         // 核心条件1：日线强信号（买入基础）
            "    AND t.resonance_score >= 70 " +
            "    AND tm.resonance_signal IN (1, 3) " +      // 核心条件2：分时强信号（精准时机）
            "    AND tm.resonance_score >= 70 " +
            "    AND m.volume_ratio > 1.5 " +               // 核心条件3：量比>1.5（真放量，复用已有字段）+ 持续5分钟验证
            "    AND EXISTS ( " +
            "        SELECT 1 FROM stock_kline_minute m2 " +
            "        WHERE m2.stock_code = m.stock_code  " +
            "          AND m2.trade_date = CURDATE() " +
            "          AND m2.trade_time >= DATE_SUB(m.trade_time, INTERVAL 5 MINUTE) " +
            "          AND m2.trade_time <= m.trade_time " +
            "          AND m2.volume_ratio > 1.5 " +
            "    ) " +
            "    AND t.cost_concentration < 12 " +              // 强化筹码筛选（主力控盘，胜率更高）,原15%→12%，更严格
            "    AND m.price > 8 " +                            // 原5元→8元，剔除低价垃圾股
            "    AND m.volume > 80000  " +                      // 原5万→8万，剔除低成交股
            "    AND (m.price - m.close) / m.close < 0.03 " +   // 高开<3%  剔除高开过多（避免追高）
            "ORDER BY " +
            "    CASE WHEN t.resonance_signal = 1 AND tm.resonance_signal = 1 THEN 1 ELSE 2 END ASC, " +
            "    (t.resonance_score + tm.resonance_score) DESC,  " +    // 双重评分之和
            "    t.cost_concentration ASC,  " +                         // 筹码越集中越优先
            "    m.volume_ratio DESC " +                                // 放量越明显越优先
            "LIMIT 5 ")
    void queryTechMinuteBuy();


    /**
     * 超短线双重共振卖出 (优化为10分钟持续验证)
     * 卖出逻辑：止损票立即卖出，止盈票可分批止盈，满足任一条件立即卖出（止损 > 止盈 > 顶背离 > 共振弱 > 量能萎缩）
     * 收盘前检查	14:55 持仓票若出现 “双重趋势走弱”，尾盘卖出
     */
    @Select("SELECT p.stock_code, p.buy_price, latest_min.price, latest_min.volume_ratio, latest_min.trade_time AS, " +
            "    ROUND((latest_min.price - p.buy_price) / p.buy_price * 100, 2) AS profit, " +
            "    CASE " +
                    // 顶背离：价格创10分钟新高但量能萎缩
            "        WHEN latest_min.price > (SELECT MAX(price) FROM stock_kline_minute WHERE stock_code = p.stock_code AND trade_date = CURDATE() AND trade_time BETWEEN DATE_SUB(latest_min.trade_time, INTERVAL 10 MINUTE) AND latest_min.trade_time) " +
            "             AND latest_min.volume_ratio < (SELECT AVG(volume_ratio) FROM stock_kline_minute WHERE stock_code = p.stock_code AND trade_date = CURDATE()  " +
            "                                           AND trade_time BETWEEN DATE_SUB(latest_min.trade_time, INTERVAL 10 MINUTE) AND latest_min.trade_time) THEN '顶背离（价格涨量缩）' " +
                    // 底背离：价格创10分钟新低但量能放大
            "        WHEN latest_min.price < (SELECT MIN(price) FROM stock_kline_minute WHERE stock_code = p.stock_code AND trade_date = CURDATE() AND trade_time BETWEEN DATE_SUB(latest_min.trade_time, INTERVAL 10 MINUTE) AND latest_min.trade_time) " +
            "             AND latest_min.volume_ratio > (SELECT AVG(volume_ratio) FROM stock_kline_minute WHERE stock_code = p.stock_code AND trade_date = CURDATE()  " +
            "                                           AND trade_time BETWEEN DATE_SUB(latest_min.trade_time, INTERVAL 10 MINUTE) AND latest_min.trade_time) THEN '底背离（价格跌量增）' " +
            "        ELSE '无背离' " +
            "    END AS divergenceType, " +     // 分时背离信号计算（价格-量能背离）
            "   CASE WHEN tech_min_5.resonance_signal IN (2,4) AND tech_min_15.resonance_signal IN (2,4) THEN '共振走弱（5/15分钟均空头）' " +   // 共振走弱：短期分时+长期分时均为空头（看跌）
            "        WHEN tech_min_5.resonance_signal IN (1,3) AND tech_min_15.resonance_signal IN (1,3) THEN '共振强势（5/15分钟均多头）' " +   // 共振强势：短期分时+长期分时均为多头（看涨）
            "        ELSE '共振分歧（5/15分钟信号相反）' " +    // 共振分歧：短期和长期信号不一致
            "    END AS resonance, " +
            "   CASE WHEN ROUND((latest_min.price - p.buy_price)/p.buy_price * 100, 2) <= -2  " +
            "             OR (CASE WHEN tech_min_5.resonance_signal IN (2,4) AND tech_min_15.resonance_signal IN (2,4) THEN 1 ELSE 0 END = 1 " +
            "                 AND divergenceType = '顶背离（价格涨量缩）') THEN '【\uD83D\uDD25 最高优先级】止损≥2%/共振走弱+顶背离 → 立即卖出' " +
            "        WHEN ROUND((latest_min.price - p.buy_price)/p.buy_price*100,2) >= 4 THEN '【\uD83D\uDCC8 高优先级】浮盈≥4% → 分批止盈' " +
            "        WHEN divergenceType = '顶背离（价格涨量缩）' THEN '【⚠️ 高优先级】分时顶背离 → 立即卖出' " +
            "        WHEN latest_min.trade_time >= '14:30:00' THEN '【⏰ 中优先级】尾盘14:30后 → 无条件清仓' " +
            "        WHEN latest_min.volume_ratio < 0.8 THEN '【⚠️ 中优先级】量能萎缩 → 择机卖出' " +
            "        WHEN resonance = '共振分歧（5/15分钟信号相反）' THEN '【⚠️ 中优先级】共振分歧 → 观望减仓' " +
            "        WHEN resonance = '共振强势（5/15分钟均多头）' THEN '【\uD83D\uDCA4 低优先级】共振强势 → 持有观察' " +
            "        WHEN divergenceType = '底背离（价格跌量增）' THEN '【\uD83D\uDCA4 低优先级】分时底背离 → 暂时持有（止损位保护）' " +
            "        ELSE '【\uD83D\uDCA4 低优先级】无明确信号 → 关注量能/共振' " +
            "    END AS sell_reason " +
            "FROM stock_position p " +
            "   LEFT JOIN ( " +
            "       SELECT  stock_code, price, volume_ratio, trade_time " +
            "       FROM stock_kline_minute WHERE trade_date = CURDATE() " +
            "       QUALIFY ROW_NUMBER() OVER (PARTITION BY stock_code ORDER BY trade_time DESC) = 1 " +
            "   ) latest_min ON p.stock_code = latest_min.stock_code " +    // 关联最新分时行情数据（窗口函数取最新1条）
            "   LEFT JOIN ( " +
            "       SELECT stock_code, resonance_signal " +
            "       FROM stock_tech_minute " +
            "       WHERE trade_date = CURDATE() " +
            "           AND trade_time BETWEEN DATE_SUB((SELECT MAX(trade_time) FROM stock_tech_minute WHERE trade_date = CURDATE()), INTERVAL 5 MINUTE)  " +
            "                          AND (SELECT MAX(trade_time) FROM stock_tech_minute WHERE trade_date = CURDATE()) " +
            "       QUALIFY ROW_NUMBER() OVER (PARTITION BY stock_code ORDER BY trade_time DESC) = 1 " +
            "   ) tech_min_5 ON p.stock_code = tech_min_5.stock_code " +   // 5分钟级共振信号（1/3=多头，2/4=空头）
            "   LEFT JOIN ( " +
            "       SELECT stock_code, resonance_signal " +
            "       FROM stock_tech_minute " +
            "       WHERE trade_date = CURDATE() " +
            "           AND trade_time BETWEEN DATE_SUB((SELECT MAX(trade_time) FROM stock_tech_minute WHERE trade_date = CURDATE()), INTERVAL 15 MINUTE)  " +
            "                          AND (SELECT MAX(trade_time) FROM stock_tech_minute WHERE trade_date = CURDATE()) " +
            "       QUALIFY ROW_NUMBER() OVER (PARTITION BY stock_code ORDER BY trade_time DESC) = 1 " +
            "   ) tech_min_15 ON p.stock_code = tech_min_15.stock_code" +  // 15分钟级共振信号（1/3=多头，2/4=空头）
            "WHERE p.status = 1 AND p.strategy = 2 " +          // 1=隔夜持仓 2=日内短线
            "ORDER BY " +
            "    CASE " +
            "        WHEN ROUND((latest_min.price - p.buy_price)/p.buy_price*100,2) <= -2 " +
            "             OR (tech_min_5.resonance_signal IN (2,4) AND tech_min_15.resonance_signal IN (2,4) AND divergenceType = '顶背离（价格涨量缩）') THEN 1 " +
            "        WHEN ROUND((latest_min.price - p.buy_price)/p.buy_price*100,2) >= 4 THEN 2 " +
            "        WHEN divergenceType = '顶背离（价格涨量缩）' THEN 3 " +
            "        WHEN latest_min.trade_time >= '14:30:00' THEN 4 " +
            "        ELSE 5 " +
            "    END ASC ")
    void queryTechMinuteSell();


    @Select("SELECT p.stock_code, p.buy_price, m.price, m.volume_ratio, m.volume, tm.resonance_signal, t.cost_concentration," +
            "   ROUND((m.price - p.buy_price) / p.buy_price * 100, 2) AS profit, " + // 盈亏比%
            "   CASE WHEN (m.price - p.buy_price) / p.buy_price <= -0.03 THEN '止损（亏损≥3%，持续10分钟）' " +
            "        WHEN (m.price - p.buy_price) / p.buy_price >= 0.05 THEN '止盈（盈利≥5%，持续10分钟）' " +
            "        WHEN t.divergence_type = 1 AND tm.macd_dif < tm.macd_dea THEN '双重顶背离（持续10分钟）' " +
            "        WHEN t.resonance_signal = 2 OR tm.resonance_signal = 2 THEN '共振卖出信号（持续10分钟）' " +
            "        WHEN t.resonance_signal = 4 AND tm.resonance_signal = 4 THEN '双重趋势走弱（持续10分钟）' " +
            "        WHEN m.volume_ratio < 0.8 THEN '量能萎缩（量比<0.8，持续10分钟）' " +
            "        ELSE '无卖出信号' " +
            "    END AS sell_reason," +                         // 卖出原因
            "    TIMESTAMPDIFF(MINUTE, " +
            "        (SELECT MIN(trade_time) " +
            "           FROM stock_kline_minute m2  " +
            "           WHERE m2.stock_code = m.stock_code AND m2.trade_date = CURDATE()  " +
            "           AND ( " +
            "             (m2.price - p.buy_price) / p.buy_price <= -0.03 " +
            "             OR (m2.price - p.buy_price) / p.buy_price >= 0.05 " +
            "             OR (t.divergence_type = 1 AND EXISTS (SELECT 1 FROM stock_tech_minute tm3 WHERE tm3.stock_code = m2.stock_code AND tm3.trade_date = CURDATE() AND tm3.trade_time = m2.trade_time AND tm3.macd_dif < tm3.macd_dea)) " +
            "             OR t.resonance_signal = 2 OR EXISTS (SELECT 1 FROM stock_tech_minute tm3 WHERE tm3.stock_code = m2.stock_code AND tm3.trade_date = CURDATE() AND tm3.trade_time = m2.trade_time AND tm3.resonance_signal = 2) " +
            "             OR (t.resonance_signal = 4 AND EXISTS (SELECT 1 FROM stock_tech_minute tm3 WHERE tm3.stock_code = m2.stock_code AND tm3.trade_date = CURDATE() AND tm3.trade_time = m2.trade_time AND tm3.resonance_signal = 4)) " +
            "             OR m2.volume_ratio < 0.8 " +
            "           )), " +
            "        m.trade_time) AS keep_minutes  " +         // 信号持续时长_分钟
            "FROM stock_position p " +
            "       LEFT JOIN v_stock_kline_minute_latest m ON p.stock_code = m.stock_code " +
            "       LEFT JOIN stock_tech_daily t ON p.stock_code = t.stock_code AND t.trade_date = CURDATE() " +
            "       LEFT JOIN stock_tech_minute tm ON p.stock_code = tm.stock_code AND tm.trade_date = CURDATE() AND tm.trade_time = m.trade_time " +
            "WHERE p.status = 1 AND p.strategy = 2 " +          // 1=隔夜持仓 2=日内短线
            "    AND ( " +                                      // 核心：触发任一卖出条件且持续10分钟
            "        ((m.price - p.buy_price) / p.buy_price <= -0.03) " +       // 1. 止损≥3% 且持续10分钟（不变）
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_kline_minute m2 " +
            "            WHERE m2.stock_code = m.stock_code  " +
            "              AND m2.trade_date = CURDATE() " +
            "              AND m2.trade_time >= DATE_SUB(m.trade_time, INTERVAL 10 MINUTE) " +
            "              AND m2.trade_time <= m.trade_time " +
            "              AND (m2.price - p.buy_price) / p.buy_price <= -0.03 " +
            "        ) " +
            "        OR ((m.price - p.buy_price) / p.buy_price >= 0.05) " +     // 2. 止盈≥5% 且持续10分钟（不变）
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_kline_minute m2 " +
            "            WHERE m2.stock_code = m.stock_code  " +
            "              AND m2.trade_date = CURDATE() " +
            "              AND m2.trade_time >= DATE_SUB(m.trade_time, INTERVAL 10 MINUTE) " +
            "              AND m2.trade_time <= m.trade_time " +
            "              AND (m2.price - p.buy_price) / p.buy_price >= 0.05 " +
            "        ) " +
            "        OR (t.divergence_type = 1 AND tm.macd_dif < tm.macd_dea) " +   // 3. 双重顶背离 且持续10分钟（不变）
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_tech_minute tm2 " +
            "            WHERE tm2.stock_code = tm.stock_code  " +
            "              AND tm2.trade_date = CURDATE() " +
            "              AND tm2.trade_time >= DATE_SUB(tm.trade_time, INTERVAL 10 MINUTE) " +
            "              AND tm2.trade_time <= tm.trade_time " +
            "              AND tm2.macd_dif < tm2.macd_dea " +
            "            AND t.divergence_type = 1 " +
            "        ) " +
            "        OR (t.resonance_signal = 2 OR tm.resonance_signal = 2) " +     // 4. 共振卖出信号 且持续10分钟（不变）
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_tech_minute tm2 " +
            "            WHERE tm2.stock_code = tm.stock_code  " +
            "              AND tm2.trade_date = CURDATE() " +
            "              AND tm2.trade_time >= DATE_SUB(tm.trade_time, INTERVAL 10 MINUTE) " +
            "              AND tm2.trade_time <= tm.trade_time " +
            "              AND (t.resonance_signal = 2 OR tm2.resonance_signal = 2) " +
            "        ) " +
            "        OR (t.resonance_signal = 4 AND tm.resonance_signal = 4) " +    // 5. 双重趋势走弱 且持续10分钟（不变）
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_tech_minute tm2 " +
            "            WHERE tm2.stock_code = tm.stock_code  " +
            "              AND tm2.trade_date = CURDATE() " +
            "              AND tm2.trade_time >= DATE_SUB(tm.trade_time, INTERVAL 10 MINUTE) " +
            "              AND tm2.trade_time <= tm.trade_time " +
            "              AND tm2.resonance_signal = 4 " +
            "            AND t.resonance_signal = 4 " +
            "        ) " +
            "        OR (m.volume_ratio < 0.8) " +      // 6. 量能萎缩：直接用表中volume_ratio<0.8 且持续10分钟
            "        AND EXISTS ( " +
            "            SELECT 1 FROM stock_kline_minute m2 " +
            "            WHERE m2.stock_code = m.stock_code  " +
            "              AND m2.trade_date = CURDATE() " +
            "              AND m2.trade_time >= DATE_SUB(m.trade_time, INTERVAL 10 MINUTE) " +
            "              AND m2.trade_time <= m.trade_time " +
            "              AND m2.volume_ratio < 0.8 " +
            "        ) " +
            "    ) " +
            "ORDER BY " +   // 排序：止损优先 + 信号持续时长越长越优先
            "   CASE WHEN (m.price - p.buy_price) / p.buy_price <= -0.03 THEN 1 " +
            "        WHEN (m.price - p.buy_price) / p.buy_price >= 0.05 THEN 2 " +
            "        WHEN (t.divergence_type = 1 AND tm.macd_dif < tm.macd_dea) THEN 3 " +
            "        WHEN t.resonance_signal = 2 OR tm.resonance_signal = 2 THEN 4 " +
            "        ELSE 5 " +
            "    END ASC, " +
            "    keep_minutes DESC;")
    void queryTechMinuteSellWarn();

}
