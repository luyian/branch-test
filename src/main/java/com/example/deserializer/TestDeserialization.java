package com.example.deserializer;

import com.example.utils.ObjectDeserializer;

import java.util.Map;

public class TestDeserialization {
    public static void main(String[] args) {
        // 你提供的序列化数据
        String hexData = "\\xac\\xed\\x00\\x05sr\\x00/com.scm.outinterface.model.gudewei.GuDeWeiToken%\\x93\\xa0\\x8b\\x1cr}\\xf6\\x02\\x00\\x06L\\x00\\aexpiredt\\x00\\x13Ljava/lang/Integer;L\\x00\\bfailCodeq\\x00~\\x00\\x01L\\x00\\agettimet\\x00\\x10Ljava/lang/Long;L\\x00\\amessaget\\x00\\x12Ljava/lang/String;L\\x00\\asuccessq\\x00~\\x00\\x03L\\x00\\x05tokenq\\x00~\\x00\\x03xpsr\\x00\\x11java.lang.Integer\\x12\\xe2\\xa0\\xa4\\xf7\\x81\\x878\\x02\\x00\\x01I\\x00\\x05valuexr\\x00\\x10java.lang.Number\\x86\\xac\\x95\\x1d\\x0b\\x94\\xe0\\x8b\\x02\\x00\\x00xp\\x00\\x00\\x1c psr\\x00\\x0ejava.lang.Long;\\x8b\\xe4\\x90\\xcc\\x8f#\\xdf\\x02\\x00\\x01J\\x00\\x05valuexq\\x00~\\x00\\x06\\x00\\x00\\x01\\x9b\\xe9\\xab\\xbc\\x7fppt\\x00\\x80SzAavxpO0b5GTOvNhXvumTau2Wdw2LdUZ+GH22uBvcDvszcpUZgkh9sf/quWRcKbh6e1MPAFj6ZjTrNkOoA3HxqlLXnTwUOfySM7AYlHLazEBB64JBLZXH7dxFw3T//W";

        System.out.println("开始反序列化 GuDeWeiToken 对象...");
        
        // 使用工具类进行反序列化
        Map<String, Object> result = ObjectDeserializer.deserializeFromHexString(hexData);
        
        System.out.println("反序列化完成，结果如下：");
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\n使用方法：");
        System.out.println("Map<String, Object> result = ObjectDeserializer.deserializeFromHexString(yourHexString);");
        
        // 访问具体字段
        System.out.println("\n具体字段值：");
        System.out.println("Token: " + result.get("token"));
        System.out.println("Expired: " + result.get("expired"));
        System.out.println("GetTime: " + result.get("gettime"));
    }
}