package com.deepthinking.common.constant;

import com.deepthinking.common.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MarketType {

    public static final String MARKET_SZ = "SZ", MARKET_SH = "SH", MARKET_BJ = "BJ", MARKET_HK = "HK";
    public static final String MARKET_CODE_SZ = "0", MARKET_CODE_SH = "1", MARKET_CODE_BJ = "2", MARKET_CODE_HK = "116";

    public static final BigDecimal LIMIT_FIVE = BigDecimal.valueOf(0.05);
    public static final BigDecimal LIMIT_TEN = BigDecimal.valueOf(0.1);
    public static final BigDecimal LIMIT_TWENTY = BigDecimal.valueOf(0.2);
    public static final BigDecimal LIMIT_THIRTY = BigDecimal.valueOf(0.3);


    /**
     * 第1位标识证券大类，第2位标识该大类下的衍生证券
     * 新股申购：以730打头。配股代码：沪市以700打头，深市以080打头。
     */
    private static final Map<String, StockExchange> markets = new HashMap<String, StockExchange>() {{
        put("000", StockExchange.builder().name("深证A股").market(MARKET_SZ).marketCode(MARKET_CODE_SZ).changeLimits(LIMIT_TEN).build());
        put("001", StockExchange.builder().name("深证A股").market(MARKET_SZ).marketCode(MARKET_CODE_SZ).changeLimits(LIMIT_TEN).build());
        put("002", StockExchange.builder().name("深证A股").market(MARKET_SZ).marketCode(MARKET_CODE_SZ).changeLimits(LIMIT_TEN).build());
        put("003", StockExchange.builder().name("深证A股").market(MARKET_SZ).marketCode(MARKET_CODE_SZ).changeLimits(LIMIT_TEN).build());
        put("300", StockExchange.builder().name("创业板").market(MARKET_SZ).marketCode(MARKET_CODE_SZ).changeLimits(LIMIT_TWENTY).build());
        put("600", StockExchange.builder().name("上证A股").market(MARKET_SH).marketCode(MARKET_CODE_SH).changeLimits(LIMIT_TEN).build());
        put("601", StockExchange.builder().name("上证A股").market(MARKET_SH).marketCode(MARKET_CODE_SH).changeLimits(LIMIT_TEN).build());
        put("603", StockExchange.builder().name("上证A股").market(MARKET_SH).marketCode(MARKET_CODE_SH).changeLimits(LIMIT_TEN).build());
        put("605", StockExchange.builder().name("上证A股").market(MARKET_SH).marketCode(MARKET_CODE_SH).changeLimits(LIMIT_TEN).build());
//        put("688", StockExchange.builder().name("科创板").market(MARKET_SH).marketCode(MARKET_CODE_SH).changeLimits(LIMIT_TWENTY).build());
//        put("920", StockExchange.builder().name("北证A股").market(MARKET_BJ).marketCode(MARKET_CODE_BJ).changeLimits(LIMIT_THIRTY).build());
    }};

    public static boolean contains(String stockCode, String stockName) {
        return markets.containsKey(stockCode.substring(0, 3)) && !StringUtil.contains(stockName, "ST");
    }

    public static String getMarket(String stockCode) {
        return markets.get(stockCode.substring(0, 3)).getMarket();
    }

    public static String getMarketCode(String stockCode) {
        if (stockCode.startsWith("BK")) {
            return "90";
        }
        return markets.get(stockCode.substring(0, 3)).getMarketCode();
    }

    public static BigDecimal getChangeLimit(String stockCode) {
        return markets.get(stockCode.substring(0, 3)).getChangeLimits();
    }


    public static Map<String, String> stockCodeMap(String stockCode) {
        return new HashMap<String, String>() {{
            put("stockCode", stockCode);
            put("market", getMarket(stockCode));
            put("marketCode", getMarketCode(stockCode));
        }};
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class StockExchange {
        private String name;
        private String market;
        private String marketCode;
        private BigDecimal changeLimits;
    }
}
