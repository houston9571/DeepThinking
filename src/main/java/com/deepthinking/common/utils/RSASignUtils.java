package com.deepthinking.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;


@Slf4j
public class RSASignUtils {


    public static final String ALGORITHM = "RSA";


    public static String encrypt(String plainText, String publicKeyStr) throws Exception {
        return encrypt(plainText, getPublicKey(publicKeyStr));
    }


    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] data = cipher.doFinal(plainText.getBytes());
        String encryptText = Base64.encodeBase64String(data);
        log.info("RSA加密明文:{}", plainText);
        log.info("RSA加密密文:{}", encryptText);
        return encryptText;
    }

    public static String decrypt(String encryptText, String privateKeyStr) throws Exception {
        return decrypt(encryptText, getPrivateKey(privateKeyStr));
    }

    public static String decrypt(String encryptText, PrivateKey privateKey) throws Exception {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] data = Base64.decodeBase64(encryptText);
            String plainText = new String(cipher.doFinal(data));
            log.info("RSA解密密文:{}", encryptText);
            log.info("RSA解密明文:{}", plainText);
            return plainText;
    }


    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr)));
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(new X509EncodedKeySpec(Base64.decodeBase64(privateKeyStr)));
    }


    public static String[] genKeyArray() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(1024, new SecureRandom());
        // 生成一对公私钥
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥和私钥
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        // 将公私钥编码为Base64字符串
        String publicKeyStr = Base64.encodeBase64String(publicKey);
        String privateKeyStr = Base64.encodeBase64String(privateKey);
        return new String[]{publicKeyStr, privateKeyStr};
    }

    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(1024, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

}
