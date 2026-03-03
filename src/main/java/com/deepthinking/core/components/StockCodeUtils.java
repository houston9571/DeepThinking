package com.deepthinking.core.components;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.core.components.StockConstants.FIELD_NAME;

public class StockCodeUtils {

    /**
     * SZ 0 深圳证券交易所 深市A股、创业板、北交所
     * SH 1 上海证券交易所 沪市A股、科创板
     * @param symbol 股票代码，如 000001
     * @return secid，如 0.000001
     */
    public static String buildSecId(String symbol) {
        return (symbol.startsWith("6") ? 1 : 0) + "." + symbol;
    }

    public static String buildSecIds(String symbols) {
        if (StrUtil.contains(symbols, COMMA)) {
            ArrayList<String> list = Lists.newArrayList();
            Arrays.stream(symbols.split(COMMA)).collect(Collectors.toList()).forEach(s -> {
                list.add(buildSecId(s));
            });
            return String.join(COMMA, list);
        }
        return buildSecId(symbols);
    }

    public static String buildFields(int end) {
        ArrayList<String> list = Lists.newArrayList();
        for (int i = 1; i <= end; i++) {
            list.add("f" + i);
        }
        return String.join(COMMA, list);
    }

    public static String buildFieldsAll() {
        return String.join(COMMA, FIELD_NAME.keySet());
    }

    /**
     * 解析secid
     *
     * @param secId secid，如 0.000001
     * @return [market, symbol]
     */
    public static String[] parseSecId(String secId) {
        String[] parts = secId.split("\\.");
        if (parts.length == 2) {
            String market = "0".equals(parts[0]) ? "SZ" : "SH";
            return new String[]{market, parts[1]};
        }
        return new String[]{"", ""};
    }

    /**
     * 验证股票代码
     */
    public static boolean isValidSymbol(String symbol) {
        if (symbol == null || symbol.length() != 6) {
            return false;
        }

        try {
            Integer.parseInt(symbol);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
