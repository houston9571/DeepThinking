package com.deepthinking.common.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class IPUtils {

    public static String getIpAddr(HttpServletRequest request) {
        String unknown = "unknown";
        String ip = request.getHeader("proxy_add_x_forwarded_for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (StrUtil.isNotBlank(ip) && ip.length() > 15 && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }

    public static String getRandomIp() {
// 需要排除监控的ip范围

        int[][] range = {{607649792, 608174079}, // 36.56.0.0-36.63.255.255

                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255

                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255

                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255

                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255

                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255

                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255

                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255

                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255

                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255

        };

        Random rdint = new Random();

        int index = rdint.nextInt(10);

        String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));

        return ip;

    }

    /*

     * 将十进制转换成IP地址

     */

    public static String num2ip(int ip) {
        int[] b = new int[4];

        String x = "";

        b[0] = (ip >> 24) & 0xff;

        b[1] = (ip >> 16) & 0xff;

        b[2] = (ip >> 8) & 0xff;

        b[3] = ip & 0xff;

        x = b[0] + "." + b[1] + "." + b[2] + "." + b[3];

        return x;

    }

    /**
     * *IP数据库地址
     **/
    private static final String DB_PATH = "./ip2region.db";

    /***
     * 查询IP归属地
     */
   /* public static DataBlock ipRegin(String ip) {
        DataBlock dataBlock = null;
        DbSearcher searcher = null;
        int algorithm = DbSearcher.BTREE_ALGORITHM;
        try {
            DbConfig config = new DbConfig();
            searcher = new DbSearcher(config, DB_PATH);
            Method method = null;
            switch (algorithm) {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
                default:
                    break;
            }
            double sTime = 0, cTime = 0;
            sTime = System.nanoTime();
            dataBlock = (DataBlock) method.invoke(searcher, ip);
            cTime = (System.nanoTime() - sTime) / 1000000;
            log.debug("IP 查询 {} in {}ms", dataBlock, cTime);
        } catch (Exception e) {
            log.error("IP 查询异常:", e);
        } finally {
            if (null != searcher) {
                try {
                    searcher.close();
                } catch (IOException ignored) {
                }
            }
        }
        return dataBlock;
    }*/

}
