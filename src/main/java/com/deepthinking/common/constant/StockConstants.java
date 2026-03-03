package com.deepthinking.common.constant;

import java.util.HashMap;
import java.util.Map;

public interface StockConstants {

    String USER_TOKEN = "fa5fd1943c7b386f172d6893dbfba10b";
    String UT = "ut";           //用户令牌（固定值）
    String SECID = "secid";     //证券ID，格式：市场.代码
    String FIELDS = "fields";   //返回字段列表，逗号分隔
    String FIELDS1 = "fields1"; // 请求字段组1
    String FIELDS2 = "fields2"; // 请求字段组2
    String INVT = "invt";       //投资类型，默认2
    String CB = "cb";           //JSONP回调函数名
    String FLTT = "fltt";       //小数位数，2表示保留2位

    String KLT = "klt";         //K线周期
    String FQT = "fqt";         //复权类型

    String BEG = "beg";         //开始日期，格式YYYYMMDD，0表示最早
    String END = "end";         //结束日期，格式YYYYMMDD，20500000表示最晚
    String PO = "po";           //排序方式，1升序，-1降序
    String PZ = "pz";           //每页条数，默认20
    String PN = "pn";           //页码，从1开始
    String LMT = "lmt";         //返回数据条数

    // K线类型
    int KLINE_DAILY = 101;      // 日线
    int KLINE_WEEKLY = 102;     // 周线
    int KLINE_MONTHLY = 103;    // 月线
    int KLINE_1MIN = 1;         // 1分钟
    int KLINE_5MIN = 5;         // 5分钟
    int KLINE_15MIN = 15;       // 15分钟
    int KLINE_30MIN = 30;       // 30分钟
    int KLINE_60MIN = 60;       // 60分钟

    // 复权类型
    int ADJUST_NONE = 0;        // 不复权
    int ADJUST_PRE = 1;         // 前复权
    int ADJUST_POST = 2;        // 后复权

    Map<String, String> FIELD_MAP = new HashMap<String, String>() {{
        put("f2", "最新价");
        put("f3", "涨跌幅");
        put("f4", "涨跌额");
        put("f5", "成交量");
        put("f6", "成交额");
        put("f7", "振幅");
        put("f8", "换手率");
        put("f9", "市盈率");
        put("f10", "量比");
        put("f12", "代码");
        put("f13", "市场");
        put("f14", "名称");

    }};

    Map<String, String> FIELD_NAME = new HashMap<String, String>() {{
        // 基础行情
        put("f1", "未知");
        put("f2", "最新价");
        put("f3", "涨跌幅");
        put("f4", "涨跌额");
        put("f5", "成交量");
        put("f6", "成交额");
        put("f7", "振幅");
        put("f8", "换手率");
        put("f9", "市盈率(动)");
        put("f10", "量比");
        put("f11", "5分钟涨跌");
        // 代码信息
        put("f12", "代码");
        put("f13", "市场");
        put("f14", "名称");
        put("f15", "最高");
        put("f16", "最低");
        put("f17", "今开");
        put("f18", "昨收");
        // 市值信息
        put("f20", "总市值");
        put("f21", "流通市值");
        put("f22", "涨速");
        put("f23", "市净率");
        put("f24", "60日涨跌幅");
        put("f25", "年初至今涨跌幅");
        // 财务指标
        put("f37", "ROE");
        put("f38", "每股净资产");
        put("f39", "股息率");
        put("f40", "股息(TTM)");
        put("f41", "ROA");
        // 扩展行情
        put("f43", "最新价");          //价格类字段通常是实际价格的100倍
        put("f44", "最高");
        put("f45", "最低");
        put("f46", "今开");
        put("f47", "成交量");
        put("f48", "成交额");
        put("f49", "昨收");
        put("f50", "量比");

        put("f51", "涨停价");
        put("f52", "跌停价");
        // 时间相关
        put("f55", "均价");
        put("f57", "代码");
        put("f58", "名称");

        // 涨跌信息
        put("f92", "涨跌");
        put("f93", "涨跌%");
        put("f94", "涨速");
        put("f95", "5分钟涨跌");
        put("f96", "60日涨跌%");
        put("f97", "今年涨跌%");
        put("f98", "上市以来涨跌%");
        // 资金流向
        put("f100", "主力净流入");
        put("f101", "主力净占比");
        put("f102", "超大单净流入");
        put("f103", "超大单净占比");
        put("f104", "大单净流入");
        put("f105", "大单净占比");
        put("f106", "中单净流入");
        put("f107", "中单净占比");
        put("f108", "小单净流入");
        put("f109", "小单净占比");
        // 委托信息
        put("f110", "买一价");
        put("f111", "买一量");
        put("f112", "买二价");
        put("f113", "买二量");
        put("f114", "买三价");
        put("f115", "买三量");
        put("f116", "买四价");
        put("f117", "买四量");
        put("f118", "买五价");
        put("f119", "买五量");
        put("f120", "卖一价");
        put("f121", "卖一量");
        put("f122", "卖二价");
        put("f123", "卖二量");
        put("f124", "卖三价");
        put("f125", "卖三量");
        put("f126", "卖四价");
        put("f127", "卖四量");
        put("f128", "卖五价");
        put("f129", "卖五量");
        // 时间戳
        put("f130", "更新时间");
        put("f131", "日期");
        put("f132", "时间");
        // 更多字段...
        put("f167", "市净");  // Price-to-Book Ratio
        put("f168", "换手率");
        put("f169", "涨跌额");
        put("f170", "涨跌幅"); //235 表示涨跌幅为 +2.35%
        put("f171", "振幅");
        put("f172", "均价");
        put("f173", "内盘");
        put("f174", "外盘");
        put("f175", "量比");
        put("f176", "委比");
        put("f177", "委差");
        put("f178", "净资");
        put("f179", "收益");
        put("f180", "总营收");
        put("f181", "毛利率");
        put("f182", "净利率");
        put("f183", "ROE加权");
        put("f184", "负债率");
    }};
}
