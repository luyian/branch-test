package com.example.deserializer;

import org.apache.commons.codec.binary.Base64;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 禾望签名
 */
public class HeWangSignEncode {
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String CHARSET = "utf-8";

    public static void main(String[] args) throws  Exception{
        String privateKey ="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJU5i042BVwgcct6KQHfkTn27XGw0rq6hprXSZw/PhQBt8GHlw4vOBehOXY25d0ogQ8CT8ZODiItpoQlvoEw8h1AwEwL9iyJw9eWguCwD8yzRBLeUkSGd1bJSNIH3Hx29i8/H3JW1eU3KM5/dVEuYn8zd3ePAp0BhodKm88PatX3AgMBAAECgYBHYC3crAQmS7KCZB0HM1twgUaTkcNJ43wMxhsEbE+SHDw7ilJbS4Sl8MzeWWXbQctxVWa48I7cFD/Ih5E2lCXJ0zvfRfypkX5D1THxnWOfrmsoaPE47rTUlB5HNhihWbQZO3yar5hFFqzSkXsFCs/9n0Roe7zaF4m4P5q6LQEP2QJBAMPckK1O+RNPxlOrXPg4HVL6JSpC07IoxWIuy3O8TBUNjnkJckZbiS865oSqguScJCqB7yQCJDRNwtDVIaPVHDUCQQDDCygHNpxtag1tW/iSb/Oh9UhYHXB8peRDElJlDfDolM5Y2aOdwuojqftuHeneSxOTH0yWgLsDf7Rq2xZyETb7AkBNnO+v3aWR44Dh0vwqWII2SW3Ey2p8JO/iskxo9mrnxNF6YSXpf4hjMOH8HF12HfGu7oJHMJoMJ+xVOL/13hMpAkBpEteLm0YGQ73cDlIJQbp0o9lHbwSsggpIf9RXkH0aLyBDCWx1jw3oDKjhF8hn8vYmqhPueIdHSUL+1exvowqFAkBUSflZiDkO6vDJkA4a5pPqXphVwNZE35a5Pc5o0SSIanj+LyfUcCM1A6U3XqiKYuMk4QTU64NC1tq81+44ik2q";

        HashMap<String, Object> params = new HashMap<>();
        params.put("time", "2026-02-01");
        params.put("sn", "30081295A0424BH78147");
        params.put("appid", "89efc5adde4242f9b57b00b5bc4ba125");
        params.put("sign", signByRSA(params, privateKey));
        System.out.println(URLEncoder.encode( params.get("sign").toString(), "utf-8"));
    }

    public static String signByRSA(Map<String, Object> params, String privateKey) {
        try {
            String content = createLinkString(paramsFilter(params));
            //System.out.println("content=" + content);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(CHARSET));
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String createLinkString(Map<String, String> params) {
        if (params == null) {
            return "";
        }
        ArrayList<String> keys = new ArrayList(params.keySet());
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        int keyLastNum = keys.size() - 1;
        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            String value = String.valueOf(params.get(key));
            sb.append(key).append("=").append(value);
            if (i != keyLastNum) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    private static Map<String, String> paramsFilter(Map<String, Object> params) {
        Map<String, String> result = new HashMap<>();
        if (params == null || params.size() == 0) {
            return result;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null || "sign".equals(entry.getKey())) {
                continue;
            }
            result.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return result;
    }
}
