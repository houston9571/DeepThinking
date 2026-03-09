package com.deepthinking.common.constant;

import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.deepthinking.common.enums.DateFormatEnum.DATE;

@Slf4j
public class MarketType {

    public static final Map<String, String> HOLIDAYS = new HashMap<String, String>() {{
        put("2026-01-01", "元旦");
        put("2026-01-02", "元旦");
        put("2026-01-03", "元旦");
        put("2026-02-17", "春节");
        put("2026-02-18", "春节");
        put("2026-02-19", "春节");
        put("2026-02-20", "春节");
        put("2026-02-21", "春节");
        put("2026-02-22", "春节");
        put("2026-02-23", "春节");
        put("2026-04-04", "清明节");
        put("2026-04-05", "清明节");
        put("2026-04-06", "清明节");
        put("2026-05-01", "劳动节");
        put("2026-05-02", "劳动节");
        put("2026-05-03", "劳动节");
        put("2026-06-19", "端午节");
        put("2026-06-20", "端午节");
        put("2026-06-21", "端午节");
        put("2026-10-01", "国庆节");
        put("2026-10-02", "国庆节");
        put("2026-10-03", "国庆节");
        put("2026-10-04", "国庆节");
        put("2026-10-05", "国庆节");
        put("2026-10-06", "国庆节");
        put("2026-10-07", "国庆节");
    }};

    public static final LocalTime MORNING_OPEN = DateUtils.parseLocalTime("09:30:00", DateFormatEnum.TIME);
    public static final LocalTime MORNING_CLOSE = DateUtils.parseLocalTime("11:30:00", DateFormatEnum.TIME);
    public static final LocalTime AFTERNOON_OPEN = DateUtils.parseLocalTime("13:00:00", DateFormatEnum.TIME);
    public static final LocalTime AFTERNOON_CLOSE = DateUtils.parseLocalTime("15:00:00", DateFormatEnum.TIME);

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

    /**
     * 不包含688 920 ST
     */
    public static boolean contains(String stockCode, String stockName) {
        return markets.containsKey(stockCode.substring(0, 3)) && !stockName.contains("ST");
    }

    public static String getMarket(String stockCode) {
        return markets.get(stockCode.substring(0, 3)).getMarket();
    }

    public static String getMarketCode(String stockCode) {
//        if (stockCode.startsWith("BK")) {
//            return "90";
//        }
        StockExchange e = markets.get(stockCode.substring(0, 3));
        if (e == null) {
            log.error("getMarketCode is null. {}", stockCode);
            return stockCode.startsWith("60") ? MARKET_CODE_SH : MARKET_CODE_SZ;
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


    public static String getTradeDateStr() {
        return DateUtils.format(LocalDate.now(), DateFormatEnum.DATE);
    }

    public static boolean isTradeDate() {
        return isTradeDate(LocalDate.now());
    }

    public static boolean isTradeDate(LocalDate date) {
        return (date.getDayOfWeek().getValue() <= 5) && !HOLIDAYS.containsKey(DateUtils.format(date, DATE));
    }

    public static boolean isTradeTime() {
        return isTradeTime(LocalTime.now());
    }

    public static boolean isTradeTime(LocalTime time) {
        time = time.withSecond(0);
        return isTradeDate() && ((!time.isBefore(MORNING_OPEN) && !time.isAfter(MORNING_CLOSE)) || (!time.isBefore(AFTERNOON_OPEN) && !time.isAfter(AFTERNOON_CLOSE)));
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
