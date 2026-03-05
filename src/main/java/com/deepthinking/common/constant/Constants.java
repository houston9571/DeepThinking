package com.deepthinking.common.constant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;


public interface Constants {


    String DEV = "dev";

    String APP_INFO = "APP_INFO";

    String ZONE_ID = "Asia/Shanghai";

    int MAX_PAGE_SIZE = 10000;

    short YES = 1;
    short NO = 0;

    short ENABLE = 1;
    short DISABLED = 0;
    short DELETED = -1;


    short EXPAND = 1;
    short SHRINK = -1;

    // 背离类型常量
    short DIVERGENCE_NONE = 0;
    short DIVERGENCE_TOP = 1;
    short DIVERGENCE_BOTTOM = 2;

    short OPERATING_BUY = 1;
    short OPERATING_SELL = 2;
    short OPERATING_WATCH = 3;

    short SIGNAL_NONE = 1;
    short SIGNAL_WEAK = 2;
    short SIGNAL_LOW = 3;
    short SIGNAL_MEDIUM = 4;
    short SIGNAL_HIGH = 5;
    short SIGNAL_HIGHEST = 6;

    BigDecimal HUNDRED = new BigDecimal(100);

    BigDecimal THOUSAND = new BigDecimal("1000");


    // 默认精度：4位小数
    int SCALE2 = 2;
    int SCALE4 = 4;
    // 四舍五入模式
    RoundingMode ROUND_MODE = HALF_UP;

    String DATETIME_REGEXP = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String POSITIVE_TIPS = "必须正整数";

    String EMPTY_TIPS = " can`t empty";

    String DEFAULT_TYPE = "-1";

    String ID = "id";

    String NA = "NA";

    String OK = "OK";

    String FAILED = "failed";

    String PROCESSING = "processing";

    String LABEL_CODE = "code";

    String LABEL_RESULT = "result";

    String LABEL_DATA = "data";

    String LABEL_TOTAL = "total";


    int THREE_MINUTES = 180;

    int THIRTY_SECONDS = 30;

    int THIRTY_MINUTES = 1800;

    String MIXED_CODE = "@&19^";

    String PERCENT = "%";

}
