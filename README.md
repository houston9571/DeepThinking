
Deep Think Quant System

1. 股票基本信息,所属概念


2. 股票实时交易行情,实时资金流向


3. 龙虎榜信息，主力买入





########### 数据同步策略 ############

2. 龙虎榜数据
   龙虎榜机构列表更新 17:00  127.0.0.1/task/dragon/dept/2026-02-10  -- dragon_dept org_dept
   龙虎榜个股列表更新 17:10  127.0.0.1/task/dragon/stock/2026-02-10  -- dragon_stock dragon_stock_detail

2. 板块列表 
   每5分钟  更新top2  筛选股票入股票池   concept_daily  concept_stock  stock_pools
   15：10  全量更新   127.0.0.1/task/concept/daily -- concept_daily concept_stock

3. 股票列表 9:46 10:16 10:46 11:16 11:32    13:16 13:46 14:16 14:46 15:02
   127.0.0.1/task/stock/daily -- stock_kline_daily    筛选股票入股票池 stock_pools

4.  