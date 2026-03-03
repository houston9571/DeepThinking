package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.DragonStockDetail;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DragonStockDetailMapper extends BaseMapper<DragonStockDetail> {



    @Select("SELECT partner_code, partner_name, stock_code, stock_name, trade_date, close, change_percent, " +
            "SUM(net_buy_amount) net_buy_amount, SUM(total_net_buy_ratio) total_net_buy_ratio, SUM(buy_amount) buy_amount, SUM(sell_amount) sell_amount " +
            "FROM ( " +
            " SELECT a.partner_code, a.partner_name, c.stock_code, c.stock_name, c.trade_date, c.close, c.change_percent, " +
            " c.net_buy_amount, c.total_net_buy_ratio, c.buy_amount, c.sell_amount " +
            " FROM org_partner a LEFT JOIN org_partner_dept b ON a.partner_code=b.partner_code " +
            "LEFT JOIN dragon_stock_detail c ON b.dept_code=c.dept_code " +
            " WHERE c.trade_date = #{tradeDate} " +
            ") x GROUP BY partner_code, partner_name, stock_code, stock_name, trade_date, close, change_percent " +
            "ORDER BY net_buy_amount DESC ")
    List<DragonStockDetail> queryDragonStockDetailWithPartner(String tradeDate);

}
