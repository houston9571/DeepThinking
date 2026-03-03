package com.deepthinking.common.utils;

import com.deepthinking.BaseTest;
import org.junit.Test;
import org.springframework.web.util.UriUtils;

import java.util.Objects;

import static com.deepthinking.common.constant.Constants.MIXED_CODE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class EncryptUtilsTest extends BaseTest {



    /**
     * 调用商户参数解密
     */
    @Test
    public void decryptData() {
        int agentId = 105;
        long timestamp = 1765856197164L;
        String encryptData = "u6kcoQFwEGk9uXu9bOCjKA%2FM7%2BeBdb2khzSU9HLxjEysQxRq4GoF%2BmMezN61lyPd";
        String encryptKey = "Xv61HZ21vdkbsT1h";
        String aesKey = Objects.requireNonNull(EncryptUtils.md5(encryptKey + MIXED_CODE + agentId + timestamp)).substring(8, 24);
        String date = EncryptUtils.decryptAesCbc(UriUtils.decode(encryptData, UTF_8), aesKey);
        System.out.println(date);
    }



    @Test
    public void tt() {
        String data = "OVILZdrCK9Eyi4Dcb7u78g==";
        System.out.println(EncryptUtils.decryptDesEcb(data, "@)_(mt~q5vppiyty"));
    }


    @Test
    public void test() throws Exception {
        String data = "cagent=MM7_AGIN/\\\\/loginname=MM7AGIN24381/\\\\/method=lg/\\\\/actype=1/\\\\/password=1q2w3e4r5t/\\\\/oddtype=A/\\\\/cur=CNY";
        String key = "mm7Ceu%5";

        String ss = "r8Uw9CNLKerGPlC6iEAFAmgPcdFa1CwV/Q2QQqnu7m3ddKLadEjaL5AMP4hu8AxZyobk5apHr1GYxyl3JhBLm0j/vNn9e61XQWvEz80zrezddKLadEjaL1o4fus/awoV7cI9HL6vISRPeM3eZ/cP8w==";

        // 打印结果
        System.out.println("Original: " + data);
        System.out.println("Encrypted: " + EncryptUtils.encryptDesEcb(data, key));
        System.out.println("Encrypted: " + EncryptUtils.decryptDesEcb(ss, key));
        System.out.println("MD5: " + EncryptUtils.md5(ss + "mm7CcW9KZm9z"));
    }


    @Test
    public void en() {
        String s = "{\n" +
                "    \"appVer\": \"1.0.0\",\n" +
                "    \"channel\": 3,\n" +
                "    \"classifyId\": 0,\n" +
                "    \"devBand\": \"HONOR\",\n" +
                "    \"machine\": true,\n" +
                "    \"money\": 50.0,\n" +
                "    \"way\": 2,\n" +
                "    \"deviceId\": \"8a22871ae9135471\",\n" +
                "    \"ucode\": \"10020\",\n" +
                "    \"os\": \"Android\",\n" +
                "    \"osVer\": \"9\",\n" +
                "    \"devName\": \"BKL-AL00\"\n" +
                "}";
        String e = EncryptUtils.encryptXor(new String(s.getBytes(UTF_8), UTF_8), 0x19);
        System.out.println(e);

        System.out.println(EncryptUtils.decryptXor(new String(e.getBytes(UTF_8), UTF_8), 0x19));
    }


    @Test
    public void res() {
        String s = "YhMQO3p2fXw7IzssKSg7NRMQO314bXg7I3dsdXU1ExA7bXxhbTsjO0pcSkpQVlfxpp7/hYb/kY/9oZT8tIH8hbE7E2Q=";
        System.out.println(" -> " + EncryptUtils.decryptXor(new String(s.getBytes(UTF_8), UTF_8), 0x19));

    }

    @Test
    public void aes() {
        String aesKey = EncryptUtils.md5("g422d0pb216j2clm" + MIXED_CODE + 104 + "1744283046455").substring(8, 24);
        System.out.println("aesKey -> " + aesKey);
        String data = "fV%2FmpBNgMb0%2BL2%2F72fwtCyDOTVdhT7F3RXLqPeZJpWcs200W1iNkizZI1cb8ixTdkCLeBluzIpk4zep%2FENtAqw%3D%3D";
        data = UriUtils.decode(data, UTF_8);
        String en = EncryptUtils.decryptAesCbc(data, aesKey);
        System.out.println("encrypt -> " + en);


//        String date =  "{languageType=6,merchantName=nb_usd_test2,username=1000003272}";
//        String key = "3wYZAjVgPi0CIYvB";
//        String iv = "eWytpmLgWauvkHfL";
//        String en = EncryptUtils.encryptAesCbc(date, key, iv);
//        System.out.println("encrypt -> " + en);
//        System.out.println("decrypt -> " + EncryptUtils.decryptAesCbc(en, key, iv));

    }


    @Test
    public void md5() {
        String s = "secureLogin=zf840_zf840nbsw";
        String d = EncryptUtils.md5(s + "8033B4C5E8774a59");
        System.out.println(d);
    }

}
