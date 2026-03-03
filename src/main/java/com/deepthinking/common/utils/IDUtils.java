package com.deepthinking.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class IDUtils {

    private static final Snowflake snowflake = IdUtil.getSnowflake(5L, 1L);


    private static final AtomicInteger a2 = new AtomicInteger(11);

    private static final AtomicInteger a4 = new AtomicInteger(1258);

    public static String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};


    public static Long getId() {
        return snowflake.nextId();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    /**
     * uuid每组4位编码，加2位递增数随机位置
     * 重复率： 5000W 以内几乎不重复
     *
     * @return
     */
    public static String uuid10() {
        if (a2.get() >= 100)
            a2.set(13);
        StringBuilder sb = new StringBuilder();
        String uuid = uuid();
        String[] sa = {uuid.substring(0, 4), uuid.substring(4, 8), uuid.substring(8, 12), uuid.substring(12, 16),
                uuid.substring(16, 20), uuid.substring(20, 24), uuid.substring(24, 28), uuid.substring(28, 32)};
        for (String s : sa)
            sb.append(chars[Integer.parseInt(s, 16) % 62]);
        String s = String.valueOf(a2.incrementAndGet());
        sb.insert(NumberUtils.random(8), s.charAt(0));
        sb.insert(NumberUtils.random(9), s.charAt(1));
        return sb.toString();
    }

    /**
     * 6000W 内不重复
     *
     * @return
     */
    public static String uuid16() {
        if (a4.get() >= 10000)
            a4.set(1258);
        StringBuilder sb = new StringBuilder();
        String uuid = uuid() + System.nanoTime() + "123456";
        String[] sa = {uuid.substring(0, 4), uuid.substring(4, 8), uuid.substring(8, 12), uuid.substring(12, 16),
                uuid.substring(16, 20), uuid.substring(20, 24), uuid.substring(24, 28), uuid.substring(28, 32),
                uuid.substring(32, 36), uuid.substring(36, 40), uuid.substring(40, 44), uuid.substring(44, 48)};
        for (String s : sa)
            sb.append(chars[Integer.parseInt(s, 16) % 62]);
        String s = String.valueOf(a4.incrementAndGet());
        sb.insert(NumberUtils.random(12), s.charAt(0));
        sb.insert(NumberUtils.random(13), s.charAt(1));
        sb.insert(NumberUtils.random(14), s.charAt(2));
        sb.insert(NumberUtils.random(15), s.charAt(3));
        return sb.toString();
    }


}
