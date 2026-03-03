package com.deepthinking.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.deepthinking.common.constant.Constants.ROUND_MODE;


/**
 * 2015年8月7日 下午12:54:57
 * Version 1.0
 */
@Slf4j
public class NumberUtils {


    private static AtomicInteger a2 = new AtomicInteger(11);

    private static AtomicInteger a4 = new AtomicInteger(1258);

    private static SecureRandom random = new SecureRandom();

    private static StringBuffer radStr = new StringBuffer("ef67HINO0EFGopJKLM2cdPQRS34abTUVYghzWjkmn8iy5Z9ABvwxCDqrstuX");

    private static StringBuilder upperLetterAndNumStr = new StringBuilder("346789ABCDEFGHJKLMNPQRTUVWXY");  //避免看错，去掉易混淆的字符
//    private static StringBuffer upperLetterAndNumStr = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    public static String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};


    public static final BigDecimal WY = new BigDecimal("1000000000000");
    public static final BigDecimal BY = new BigDecimal("10000000000");
    public static final BigDecimal SY = new BigDecimal("1000000000");;
    public static final BigDecimal Y =  new BigDecimal("100000000");
    public static final BigDecimal BW = new BigDecimal("1000000");
    public static final BigDecimal SW = new BigDecimal("100000");
    public static final BigDecimal W =  new BigDecimal("10000");

    public static BigDecimal moneyDivide(int fee) {
        return new BigDecimal(fee).divide(new BigDecimal("100"), 2, ROUND_MODE);
    }

    public static BigDecimal moneyDivide(String fee) {
        return new BigDecimal(fee).divide(new BigDecimal("100"), 2, ROUND_MODE);
    }

    public static BigDecimal money(String fee) {
        return new BigDecimal(fee).setScale(2, ROUND_MODE);
    }


    public static String uuid() {
        return StringUtil.replaceAll(UUID.randomUUID().toString(), "-", "");
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
        sb.insert(random(8), s.substring(0, 1));
        sb.insert(random(9), s.substring(1, 2));
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
        sb.insert(random(12), s.substring(0, 1));
        sb.insert(random(13), s.substring(1, 2));
        sb.insert(random(14), s.substring(2, 3));
        sb.insert(random(15), s.substring(3, 4));
        return sb.toString();
    }

    /**
     * uuid缩短版，
     *
     * @return
     */
    public static String uuid22() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++)
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        for (int i = 8; i < 16; i++)
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        String res = Base64.getEncoder().encodeToString(buffer).replace("=", "");
        return StringUtil.replaceAll(res, "[+|/]", randomStr(1));
    }

    /**
     * 播放次数转 万 亿
     *
     * @return
     */
    public static String addCountUtil(String count) {
        return addCountUtil(count, "");
    }


    public static String addCountUtil(String count, String defaultValue) {
        if (count == null || count.equals("")) {
            return defaultValue;
        }
        if (count.endsWith("万") || count.endsWith("亿")) {
            return count;
        }
        String fh = "";
        if (count.startsWith("-")) {
            fh = "-";
            count = count.substring(1);
        }
        if (NumberUtils.isNumeric(count)) {
            BigDecimal b = new BigDecimal(count);
            if (b.compareTo(WY) >= 0) {
                return fh + b.divide(WY).setScale(1, ROUND_MODE).toPlainString() + "万亿";
            }
            if (b.compareTo(Y) >= 0) {
                if (b.compareTo(BY) >= 0) {
                    return fh + b.divide(Y).setScale(0, ROUND_MODE).toPlainString() + "亿";
                }
                if (b.compareTo(SY) < 0) {
                    return fh + b.divide(Y).setScale(2, ROUND_MODE).toPlainString() + "亿";
                }
                return fh + b.divide(Y).setScale(1, ROUND_MODE).toPlainString() + "亿";
            }
            if (b.compareTo(W) >= 0) {
                if (b.compareTo(BW) >= 0) {
                    return fh + b.divide(W).setScale(0, ROUND_MODE).toPlainString() + "万";
                }
                if (b.compareTo(SW) >= 0) {
                    return fh + b.divide(W).setScale(2, ROUND_MODE).toPlainString() + "万";
                }
                return fh + b.divide(W).setScale(1, ROUND_MODE).toPlainString() + "万";
            }
        }
        return count;
    }

    /**
     * 浮点数四舍五入
     *
     * @param f
     * @param len
     * @return
     */
    public static float toFixed(float f, int len) {
        BigDecimal b = new BigDecimal(f);
        return b.setScale(len, ROUND_MODE).floatValue();
    }

    /**
     * 生成N位大写字母和数字混合的串，一般用于邀请码等
     *
     * @param length
     * @return
     */
    public static String genNumAndUpperLetter(int length) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) {
            str.append(upperLetterAndNumStr.charAt(random.nextInt(upperLetterAndNumStr.length())));
        }
        return str.toString();
    }

    /**
     * 生成N位纯数字的串，一般用于短信验证码
     *
     * @param length
     * @return
     */
    public static String genNumber(int length) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public static int random(int scope) {
        return (int) (Math.random() * scope);
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }


    public static int randomInt() {
        return Math.abs(random.nextInt());
    }

    /**
     * 基于Base62编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public static String randomStr(int length) {
        SecureRandom random = new SecureRandom();
        StringBuffer generateRandStr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            generateRandStr.append(radStr.charAt(random.nextInt(radStr.length())));
        }
        return generateRandStr.toString();
    }

    public static char randomCharAtIndex(int index) {
        return radStr.charAt(index);
    }

    public static String getOffsetString(String str) {
        StringBuffer tmp = new StringBuffer();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            tmp.append(radStr.charAt(c[i] % 16));
        }
        return tmp.toString();
    }

    /**
     * 将B 转换为KB MB GB
     *
     * @param size
     * @return
     */
    public static String toByteSize(long size) {
        int k = 1024;
        float KB = 1024;
        float MB = KB * 1024;
        float GB = MB * 1024;
        float TB = GB * 1024;
        float PB = TB * 1024;
        float ZB = PB * 1024;
        DecimalFormat df = new DecimalFormat("0.00");
        if (size >= ZB)
            return df.format(size / ZB) + "ZB";
        if (size >= PB)
            return df.format(size / PB) + "PB";
        if (size >= TB)
            return df.format(size / TB) + "TB";
        if (size >= GB)
            return df.format(size / GB) + "GB";
        if (size >= MB)
            return df.format(size / MB) + "MB";
        if (size >= KB)
            return df.format(size / KB) + "KB";
        return size + "B ";
    }

    public static boolean isNumeric(String str) {
        if (StrUtil.isEmpty(str)) {
            return false;
        }
        String regx = "^[-\\+]?[.\\d]*$|[+-]*\\d+\\.?\\d*[Ee]*[+-]*\\d+";
        Pattern pattern = Pattern.compile(regx);
        return pattern.matcher(str).matches();
    }

    public static String accessScienceNumeric(String str) {
        try {
            if (NumberUtils.isNumeric(str) && !str.startsWith("0") && !str.equals("-") && !str.equals("+"))
                return new BigDecimal(str).toPlainString();
        } catch (Exception e) {
            log.error("---->转换数字错误 str={}", str);
        }
        return str;
    }

    public static void main(String[] args) {
        NumberUtils.uuid16();
        long ti = 0;
        long st;
        for (int ii = 1; ii <= 100; ii++) {
//            st = System.currentTimeMillis();
//            System.out.println(NumberUtils.uuid16());
//            ti += System.currentTimeMillis() - st;
            String s = String.valueOf(System.nanoTime());
            System.out.println(NumberUtils.genNumber(4));

        }
        System.out.println(ti + "ms");
    }
}
