package com.deepthinking.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    private static final String[] SCRIPT = {"bash", ".", "redirect", "url", "referer", "http", "systemd", "shell", "cd+", "rm+", "wget", "curl", "chmod", "sh+", "sql", "script>", "<script", "alert(", "iframe", "grant", "drop"};

    private static final char SEPARATOR = '_';


    public static boolean validationScript(String str) {
        return StringUtils.containsAny(str, SCRIPT);
    }

    public static String trim(String str) {
        return isEmpty(str) ? "" : str.trim();
    }

    public static String splitFirst(String str, String separatorChars) {
        if (str != null) {
            String[] ss = split(str, separatorChars);
            if (ss.length > 1)
                return ss[0];
        }
        return str;
    }


    /**
     * \n 回车( ) \t 水平制表符( ) \s 空格(\u0008) \r 换行( )
     */
    public static String replaceTab(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = Pattern.compile("[\t\r\n]").matcher(str);
            dest = m.replaceAll(" ");
        }
        return dest;
    }


    /**
     * \n 回车( ) \t 水平制表符( ) \s 空格(\u0008) \r 换行( )
     * 替换全部exp部分
     */
    public static String replaceAll(String str, String exp, String replacement) {
        String dest = "";
        if (str != null) {
            Matcher m = Pattern.compile(exp).matcher(str);
            dest = m.replaceAll(replacement);
        }
        return dest;
    }

    /**
     * 替换target一次
     *
     * @param tmp
     * @param target
     * @param replacement
     * @return
     */
    public static String replace(String tmp, CharSequence target, CharSequence replacement) {
        if (tmp.contains(target))
            return tmp.replace(target, replacement);
        return tmp;
    }

    public static String arrayToString(int... i) {
        String sb = "";
        for (Number ii : i) {
            sb += ii + ",";
        }
        return sb.length() > 1 ? sb.substring(0, sb.length() - 1) : sb;
    }


    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean containsString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.contains(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }

    public static String splicingParam(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        String param = "";
        if (!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            param = StringUtils.substringBeforeLast(sb.toString(), "&");
        }
        return param;
    }


    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        if (!s.contains("_")) {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 补充空格
     * 参数（str字符串，字段的长度）
     */
    public static String formatStr(String str, int length) throws Exception {
        if (str == null) {
            return null;
        }
        int strLen = str.getBytes("GBK").length;  //空格的长度 = 20 - 字段的长度（GBK）。
//        if (strLen == length) {
//            return str;
//        } else if (strLen < length) {
        int temp = length - strLen;
        String tem = "";
        for (int i = 0; i < length; i++) {
            tem = tem + " ";
        }
        return str + tem;
//        }else{
//            return str.substring(0,length);
//        }

    }

    /**
     * 读取 InputStream 到字符串
     */
    public static String readToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String str = result.toString(StandardCharsets.UTF_8.name());
            return str.replaceAll("^//.*", "")  // 移除单行注释
                    .replaceAll("/\\*.*?\\*/", "")  // 移除多行注释
                    .replaceAll("^\\s*#.*$", "");  // 移除YAML注释
        }
    }

    public static String joinWithIndex(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner joiner = new StringJoiner(delimiter);
        int i = 0;
        for (CharSequence cs : elements) {
            joiner.add(++i + "." + cs);
        }
        return joiner.toString();
    }

    public static void main(String[] args) throws Exception {
        String owner = "客户: 周舟";
        String worker = "施工: 刘德华 中大国际有限公司";
        System.out.println(StringUtil.formatStr(owner, 50) + "-" + owner.getBytes("GBK").length);
        System.out.println(StringUtil.formatStr(worker, 26) + "-" + worker.getBytes("GBK").length);

        String s1 = "610" + System.nanoTime();
        System.out.println(s1.length() + " " + s1);

    }
}
