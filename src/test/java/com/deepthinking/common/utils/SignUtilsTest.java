package com.deepthinking.common.utils;

import com.deepthinking.BaseTest;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;


public class SignUtilsTest extends BaseTest {


    @Test
    public void rsa() throws Exception {
        String plainText = "{dasdasdasdasdasdasdacsadnqsjjihiuhihi}";
        String privateKeyStr = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALcSdZmM3rt4DiMAemvOFJTUbifqYBAb/X7QBdxQCeU4UGD86KVw8NNODXdadoqzVdswy+NjAe+Iko4VQI1vePMVtktkGnhgx+EzZcqNQRFVj0T/lD8rks/m4gK8JBHHCqNws9zJ/G4WC8wIUAzJi2/cdotJyCy1AZBrPmlv55frAgMBAAECgYEAtoqWbiQTlvQPjIEWkFXtGbVznSNK4+U073R204WPSFrNctfbFdO2nctvC/pMxuIokqVmN3XqYTBZiYjRU/W5r1zjImsCU8EKUZyZRi6ljmKRBPa1vstGC3Bo3T6Z+9/1tcCaUxQwTK2Sbn4aL/7Df2Vh7AYrzIu2kA8Pa45vRtkCQQD30fVNGQ3bjIHx39TjXhtb9+mlDWk1gEiZIctdCi2jer133+VvyrF/K+3PovPceAy4yEoOLIC+YpoiIsZ9E313AkEAvR1j6BADiNT0/idWBhtOitsy6PBES4LEdHF6lkJijvOY3ZbHf1nKSx7UpBG0GrCSBmpUrd81G1v6D0V1NTRGLQJBANBfcPOXqmg9V5HJ09Yt7bFB3eoTQbBjoidoG/eqND+uV5tw3hlGhEJa7IXXDVcGdiP0/Re34bSzcchcFytZ9PcCQQCktsmCoRgDAMCV8LrrPLNvG7Y+zq4dOrtTVFdaMl3XdnIJZj9CO3mHbkX01PqSWIIHFmvEuOlvd+/Xhz6r5WjNAkB13ZIDZSsHVHzT39K0PRTmCHqwvcIaHYRXn0gJpewyEG3oxySK2Kk/Sh4jNaP0YWW9xnrxyNzj8JUqavr/VC/i";
        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3EnWZjN67eA4jAHprzhSU1G4n6mAQG/1+0AXcUAnlOFBg/OilcPDTTg13WnaKs1XbMMvjYwHviJKOFUCNb3jzFbZLZBp4YMfhM2XKjUERVY9E/5Q/K5LP5uICvCQRxwqjcLPcyfxuFgvMCFAMyYtv3HaLScgstQGQaz5pb+eX6wIDAQAB";
        PrivateKey privateKey = SignUtils.getRSAPrivateKeyByPriKeyStr(privateKeyStr, SignUtils.keyAlgorithm);
        String sign = SignUtils.digitalSign(plainText, privateKey, SignUtils.signAlgorithm);
        PublicKey publicKey = SignUtils.getRSAPublicKeyByPubKeyStr(publicKeyStr, SignUtils.keyAlgorithm);
        boolean flag = SignUtils.verifyDigitalSign(plainText, sign, publicKey, SignUtils.signAlgorithm);
        System.out.println(flag);

    }


    @Test
    public void rsaEn() throws Exception {
        String plainText = "GameCode=marble_rallyLang=zhMerchantCode=A1Timestamp=1726198338635";
        String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyxLqgKYp6zQQYtZYot/Zb7fNF5Ewo5EUa79L6aWo1ScdGRXL9doHNm4fPkmO6sw0CDVEUO1t2ZT6UJSt4IgXr7p7T9frlFNk+mFR+tjFZTNy0C/5mpI8nWNdbRs+US40Yues7BJHwVOMs8oljFtGhVu/UwfbWEKhqXuqS7y0Z7NC1elVH+JICJWGX3kr505yy92R35uPlrLljEdQP/ZjeJZNbvPzOVpdudGz6s8ccTr6Pl4ZMi08iWOM1+t2S7mH9kyJuC0wd5LxwkH7VVfuxfQc+ZyoliqAfWDbrvj6aKyFzH8Hj6DTbm1iin95ZJzVj7rShwEto9NC71wq0GjukQIDAQAB";
        String data = RSASignUtils.encrypt(plainText, publicKeyStr);
        System.out.println(data);
    }


    @Test
    public void genkey() throws Exception {
        String plainText = "GameCode=marble_rallyLang=zhMerchantCode=A1Timestamp=1726198338635";
        KeyPair keyPair = RSASignUtils.genKeyPair();
        String data = RSASignUtils.encrypt(plainText, keyPair.getPublic());
        System.out.println("RSA加密密文: " + data);
        String text = RSASignUtils.decrypt(data, keyPair.getPrivate());
        System.out.println("RSA解密明文: " + text);

    }


}
