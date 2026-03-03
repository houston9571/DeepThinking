package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.DragonStock;
import com.deepthinking.mysql.vo.DragonDetailStockKline;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockMapper extends BaseMapper<DragonStock> {



    @Select("SELECT a.partnerCode, a.partnerName, c.trade_date, c.dept_code, dept.name AS dept_name, " +
            "c.net_buy_amount AS partnerNetBuyAmount, c.buy_amount AS partnerBuyAmount, c.total_buy_ratio AS partnerTotalBuyRatio, " +
            "c.sell_amount AS partnerSellAmount, c.total_sell_ratio AS partnerTotalSellRatio, d.*, e.* " +
            "FROM org_partner a LEFT JOIN org_partner_dept b ON a.partner_code=b.partner_code LEFT JOIN org_dept dept ON b.dept_code=dept.dept_code " +
            "LEFT JOIN dragon_stock_detail c on b.dept_code=c.dept_code " +
            "LEFT JOIN dragon_stock d on c.stock_code=d.stock_code AND c.trade_date=d.trade_date " +
            "LEFT JOIN stock_kline_daily e on c.stock_code=e.stock_code AND c.trade_date=e.trade_date " +
            "WHERE c.trade_date=#{tradeDate}")
    List<DragonDetailStockKline> queryDragonStockList(@Param("tradeDate") String tradeDate);

    @Select("SELECT y.*, d.*, e.* " +
            "FROM ( " +
            " SELECT partner_code, partner_name, stock_code, stock_name,trade_date, SUM(net_buy_amount) partnerNetBuyAmount, SUM(total_net_buy_ratio) partnerNetBuyRatio, SUM(buy_amount) partnerBuyAmount, SUM(sell_amount) partnerSellAmount " +
            " FROM ( " +
            "  SELECT a.partner_code, a.partner_name, c.stock_code, c.stock_name, c.trade_date, c.net_buy_amount, c.total_net_buy_ratio, c.buy_amount, c.sell_amount " +
            "  FROM org_partner a LEFT JOIN org_partner_dept b ON a.partner_code=b.partner_code " +
            "  LEFT JOIN dragon_stock_detail c ON b.dept_code=c.dept_code " +
            "  WHERE c.stock_code= #{stockCode} AND c.trade_date>'2026-02-10'" +
            "  ) x GROUP BY partner_code, partner_name, stock_code, stock_name, trade_date " +
            " ORDER BY trade_date DESC " +
            ") y " +
            "LEFT JOIN dragon_stock d on y.stock_code=d.stock_code AND y.trade_date=d.trade_date " +
            "LEFT JOIN stock_kline_daily e on y.stock_code=e.stock_code AND y.trade_date=e.trade_date " +
            "LIMIT 30")
    List<DragonDetailStockKline> queryDragonStockDetail(@Param("stockCode") String stockCode);

    @Select("SELECT y.*, d.*, e.* " +
            "FROM ( " +
            " SELECT partner_code, partner_name, `code`, `name`,trade_date, SUM(net_buy_amount) partnerNetBuyAmount, SUM(total_net_buy_ratio) partnerNetBuyRatio, SUM(buy_amount) partnerBuyAmount, SUM(sell_amount) partnerSellAmount " +
            " FROM ( " +
            "  SELECT a.`code` AS partner_code, a.name AS partner_name, c.`code`, c.`name`, c.trade_date, c.net_buy_amount, c.total_net_buy_ratio, c.buy_amount, c.sell_amount " +
            "  FROM org_partner a LEFT JOIN org_partner_dept b ON a.`code`=b.partner_code LEFT JOIN dragon_stock_detail c ON b.dept_code=c.dept_code " +
            "  WHERE a.stockCode= #{stockCode} AND c.trade_date>'2026-02-10'" +
            "  ) x GROUP BY partner_code, partner_name, `code`, `name`, trade_date " +
            " ORDER BY trade_date DESC " +
            ") y " +
            "LEFT JOIN dragon_stock d on y.code=d.code AND y.trade_date=d.trade_date " +
            "LEFT JOIN stock_kline_daily e on y.code=e.code AND y.trade_date=e.trade_date " +
            "LIMIT 30")
    List<DragonDetailStockKline> queryDragonPartnerDetail(@Param("stockCode") String stockCode);


}
