package com.deepthinking.common.utils;

import cn.hutool.crypto.SecureUtil;
import com.deepthinking.common.exception.EncryptException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptUtils {


//    static {
//        Security.setProperty("crypto.policy", "unlimited");
//        Security.addProvider(new BouncyCastleProvider());
//    }

    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";
    private static final String AES_ECB = "AES/ECB/PKCS5Padding";

    private static final String DES = "DES";
    private static final String DES_ECB = "DES/ECB/PKCS5Padding";

    /**
     * AES-128-CBC 解密
     */
    public static String encryptDesEcb(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DES_ECB);
            KeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKey secretKey = SecretKeyFactory.getInstance(DES).generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException(DES_ECB + " Encrypt data failed. ", e);
        }
    }

    public static String decryptDesEcb(String encrypt, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DES_ECB);
            KeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKey secretKey = SecretKeyFactory.getInstance(DES).generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encrypt));
            return new String(decryptedData, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(DES_ECB + " Decrypt data failed. ", e);
        }
    }

    /**
     * AES-128-CBC 解密
     */
    public static String decryptAesCbc(String encrypt, String key) {
        return decryptAesCbc(encrypt, key, key.substring(0, 16));
    }

    public static String decryptAesCbc(String encrypt, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), AES), new IvParameterSpec(iv.getBytes()));
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypt));
            return new String(original, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(AES_CBC + " Decrypt data failed. ", e);
        }
    }

    /**
     * AES-128-CBC 加密
     */
    public static String encryptAesCbc(String original, String key) {
        return encryptAesCbc(original, key, key.substring(0, 16));
    }

    public static String encryptAesCbc(String original, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), AES), new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = cipher.doFinal(original.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(AES_CBC + " Encrypt data failed. ", e);
        }
    }

    // 解密 ECB
    public static String decryptAesEcb(String encrypt, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF_8), AES);
            Cipher cipher = Cipher.getInstance(AES_ECB);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encByte = Base64.getDecoder().decode(encrypt);
            byte[] original = cipher.doFinal(encByte);
            return new String(original, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(AES_ECB + " Decrypt data failed", e);
        }
    }

    // 加密 ECB
    public static String encryptAesEcb(String original, String key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ECB);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF_8), AES);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(original.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(AES_ECB + " Encrypt data failed", e);
        }
    }

    public static String md5(String input) {
        return SecureUtil.md5(input);
    }

    /**
     * 对字符数组进行散列, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
//        salt = (salt == null || salt.length < SALT.length) ? SALT : salt;
//        iterations = iterations < ITERATIONS ? ITERATIONS : iterations;
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null)
                digest.update(salt);
            byte[] result = digest.digest(input);
            for (int i = 0; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


    public static String encryptXor(String content, int pwd) {
        return Base64.getEncoder().encodeToString((xor(content.getBytes(), pwd)));
    }

    public static String decryptXor(String content, int pwd) {
        return new String(xor(Base64.getDecoder().decode(content), pwd));
    }

    private static byte[] xor(byte[] bytes, int pwd) {
        try {
            int length = bytes.length;
            for (int i = 0; i < length; ++i) {
                bytes[i] = (byte) (bytes[i] ^ pwd);
            }
            return bytes;
        } catch (Exception var4) {
            throw new EncryptException();
        }
    }


    /**
     * 签名
     *
     * @param params
     * @param key
     * @return
     */
    public static String hmacSHA256(String params, String key) {
        try {
            //还原秘钥
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            //实例化MAC
            Mac mac = Mac.getInstance("HmacSHA256");
            //初始化MAC
            mac.init(signingKey);
            //获得消息概要
            byte[] rawHmac = mac.doFinal(params.getBytes());
            //URL编码
            return byte2hex(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式。
            stmp = (Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }


}
