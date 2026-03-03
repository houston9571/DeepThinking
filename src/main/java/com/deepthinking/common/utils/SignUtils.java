package com.deepthinking.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static cn.hutool.core.util.CharsetUtil.UTF_8;


public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    //签名算法
    public static final String signAlgorithm = "SHA1WithRSA";

    //签名算法
    public static final String signAlgorithmMD5 = "MD5WithRSA";

    //秘钥算法
    public static final String keyAlgorithm = "RSA";


    /**
     * 数字签名函数入口
     *
     * @param plainText     待签名明文
     * @param privateKey    签名使用私钥
     * @param signAlgorithm 签名算法
     * @return 签名后的文本
     * @throws Exception
     */
    public static String digitalSign(String plainText, PrivateKey privateKey, String signAlgorithm) throws Exception {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            byte[] plainBytes = plainText.getBytes();
            byte[] signBytes = digitalSign(plainBytes, privateKey, signAlgorithm);
            String signText = Base64.encodeBase64String(signBytes);
            logger.info("RSA生成签名的明文：{}，密文：{}", plainText, signText);
            return signText;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效");
        } catch (SignatureException e) {
            throw new Exception("数字签名时出现异常");
        }
    }

    /**
     * 数字签名函数入口
     *
     * @param plainBytes    待签名明文字节数组
     * @param privateKey    签名使用私钥
     * @param signAlgorithm 签名算法
     * @return 签名后的字节数组
     * @throws Exception
     */
    public static byte[] digitalSign(byte[] plainBytes, PrivateKey privateKey, String signAlgorithm) throws Exception {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(plainBytes);
            byte[] signBytes = signature.sign();
            return signBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效");
        } catch (SignatureException e) {
            throw new Exception("数字签名时出现异常");
        }
    }

    /**
     * 验证数字签名函数入口
     */
    public static boolean verifyDigitalSign(String plainText, String signText, PublicKey publicKey, String signAlgorithm) throws Exception {
        logger.info("RSA验证签名的明文：{}，密文：{}", plainText, signText);
        boolean isValid = false;
        try {
            byte[] plainBytes = plainText.getBytes();
            signText = URLDecoder.decode(signText, UTF_8).replace("2B%", "+");
            byte[] signBytes = Base64.decodeBase64(signText);
            isValid = verifyDigitalSign(plainBytes, signBytes, publicKey, signAlgorithm);
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效");
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常");
        }
    }

    /**
     * 验证数字签名函数入口
     */
    public static boolean verifyDigitalSign(byte[] plainBytes, byte[] signBytes, PublicKey publicKey, String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效");
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常");
        }
    }


    /**
     * 获取RSA公钥对象
     *
     * @param pubKyeStr    RSA公钥串
     * @param keyAlgorithm 密钥算法
     * @return RSA公钥对象
     * @throws Exception
     */
    public static PublicKey getRSAPublicKeyByPubKeyStr(String pubKyeStr, String keyAlgorithm) throws Exception {
        InputStream in = null;
        try {
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(pubKyeStr));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PublicKey pubKey = keyFactory.generatePublic(pubX509);

            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("生成密钥工厂时没有[%s]此类算法", keyAlgorithm));
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成公钥对象异常");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }



    /**
     * 获取RSA私钥对象
     *
     * @param priKeyStr    RSA私钥串
     * @param keyAlgorithm 密钥算法
     * @return RSA私钥对象
     * @throws Exception
     */
    public static PrivateKey getRSAPrivateKeyByPriKeyStr(String priKeyStr, String keyAlgorithm) throws Exception {
        logger.info("RSA秘钥的key：{}", priKeyStr);
        InputStream in = null;
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKeyStr));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);

            return priKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("生成私钥对象异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成私钥对象异常");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * RSA加密
     *
     * @param plainBytes      明文字节数组
     * @param publicKey       公钥
     * @param keyLength       密钥bit长度
     * @param reserveSize     padding填充字节数，预留11字节
     * @param cipherAlgorithm 加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 加密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] RSAEncrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int encryptBlockSize = keyByteSize - reserveSize; // 加密块大小=密钥字节数-padding填充字节数
        int nBlock = plainBytes.length / encryptBlockSize;// 计算分段加密的block数，向上取整
        if ((plainBytes.length % encryptBlockSize) != 0) { // 余数非0，block数再加1
            nBlock += 1;
        }

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 输出buffer，大小为nBlock个keyByteSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            // 分段加密
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }

                // 得到分段加密结果
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(encryptedBlock);
            }

            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("加密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }

    /**
     * RSA解密
     *
     * @param encryptedBytes  加密后字节数组
     * @param privateKey      私钥
     * @param keyLength       密钥bit长度
     * @param reserveSize     padding填充字节数，预留11字节
     * @param cipherAlgorithm 加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 解密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] RSADecrypt(byte[] encryptedBytes, Key key, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int decryptBlockSize = keyByteSize - reserveSize; // 解密块大小=密钥字节数-padding填充字节数
        int nBlock = encryptedBytes.length / keyByteSize;// 计算分段解密的block数，理论上能整除

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            // 输出buffer，大小为nBlock个decryptBlockSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            // 分段解密
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                // block大小: decryptBlock 或 剩余字节数
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }

                // 得到分段解密结果
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(decryptedBlock);
            }

            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类解密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("解密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }


}
