package com.example.test;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.util.StringUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SHA256Utils {

    private static final String HMAC_SHA256 = "HmacSHA256";


    /**
     * 计算 HMAC-SHA256 签名
     * @param key  密钥 (operatorSecret)
     * @param data 待签名的数据
     * @return HMAC-SHA256 签名结果（十六进制大写字符串）
     */
    public static String getHmacSha256Str(String key, String data) {
        if (key == null || data == null) {
            return null;
        }
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 calculation failed", e);
        }
    }


    public static void main(String[] args) throws  Exception {
//        System.out.println(System.currentTimeMillis());
//
//        String operatorSecret = "XQdN7scCg1qpqfeRs3gTHMXWZUV26gu0";
//        String data = "{\"operatorID\":\"BZ175B6FDE\",\"operatorSecret\":\"XQdN7scCg1qpqfeRs3gTHMXWZUV26gu0\"}";
//
//        System.out.println(getHmacSha256Str(operatorSecret, data));

        System.out.println(encryptAesEcbBase64("Bzapi@20260325#", "jklIPOIU1234kkuu"));

        String dataStr = "{\"projectCode\":\"CP20260305000006\",\"status\":1}";
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);;
        String operatorId = "BZ7C04103A";
        String operatorSecret = "wrGkX8XMq68K73UBLfzwWXonk8pkR7go";
        String sigSecret = "4cm8JJOeriKn56oUGdkIfLwPKD2Uxx8O";
        String seq = "0001";
        System.out.println(buildRequestBody(operatorId, dataStr, timeStamp, seq, sigSecret, operatorSecret));
    }


    /**
     * AES/ECB/PKCS5Padding 加密，结果 Base64 编码（用于获取Token接口的 password 参数）。
     *
     * @param plain 明文
     * @param aesKey16 16 字节密钥（如文档约定的固定 key）
     */
    private static String encryptAesEcbBase64(String plain, String aesKey16) throws Exception {
        if (aesKey16 == null || aesKey16.length() != 16) {
            throw new IllegalArgumentException("AES key 必须为 16 字节");
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(aesKey16.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static JSONObject buildRequestBody(String operatorId, String dataStr, String timeStamp, String seq, String sigSecret,
                                               String operatorSecret) {
        String sigRaw = operatorId + dataStr + timeStamp + seq;
        String sig = hmacSha256Upper(getSigKey(sigSecret, operatorSecret), sigRaw);

        JSONObject req = new JSONObject(new LinkedHashMap<>());
        req.put("operatorID", operatorId);
        req.put("data", dataStr);
        req.put("timeStamp", timeStamp);
        req.put("seq", seq);
        req.put("sig", sig);
        return req;
    }

    private static String getSigKey(String sigSecret, String operatorSecret) {
        if (StringUtil.isNotBlank(sigSecret)) {
            return sigSecret;
        }
        return operatorSecret;
    }

    private static String hmacSha256Upper(String key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHexUpper(hash);
        } catch (Exception e) {
            return "";
        }
    }

    private static String bytesToHexUpper(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}

