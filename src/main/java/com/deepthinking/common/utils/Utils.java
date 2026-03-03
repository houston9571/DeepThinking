package com.deepthinking.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {


    public static void createZipFile(String path, File... files) {
        ZipOutputStream zip = null;
        FileOutputStream dest = null;

        try {
            File f = new File(path);
            dest = new FileOutputStream(f);
            zip = new ZipOutputStream(new BufferedOutputStream(dest));
            File[] var5 = files;
            int var6 = files.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                File file = var5[var7];
                zip.putNextEntry(new ZipEntry(file.getName()));
                zip.write(FileUtil.readBytes(file));
                zip.closeEntry();
            }
        } catch (Exception var16) {
            var16.printStackTrace();
            throw new RuntimeException("Unable to create zip file");
        } finally {
            try {
                if (zip != null) {
                    zip.close();
                }

                if (dest != null) {
                    dest.close();
                }
            } catch (Exception var15) {
            }

        }

    }



    public static double parseMoney(double fee) {
        BigDecimal b = new BigDecimal(fee);
        return b.setScale(2, 4).doubleValue();
    }

    public static double parseMoney(String fee) {
        BigDecimal b = new BigDecimal(fee);
        return b.setScale(2, 4).doubleValue();
    }

    public static final boolean getCharEq(String str) {
        int num = 0;
        char[] chars = str.toCharArray();
        char c = chars[0];
        int len = chars.length;

        for (int i = 0; i < len; ++i) {
            if (c == chars[i]) {
                ++num;
            }
        }

        return num == len;
    }

    public static String firstLetter(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        String str = PinyinUtil.getFirstLetter(key, "");
        if (StrUtil.isNotBlank(str) && str.length() > 0) {
            // 取第一个
            return str.substring(0, 1).toUpperCase();
        }
        return null;
    }

    public static long randomByUserIdAndToday(Long userId) {
        long random = 0;
        String date = DateUtil.format(new Date(), "yyyyMMdd");
        if (userId != null && userId > 0) {
            date = date + userId;
        }
        random = Long.valueOf(date);
        return random;
    }

}
