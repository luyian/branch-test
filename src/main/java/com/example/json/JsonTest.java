package com.example.json;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

import java.nio.charset.StandardCharsets;

public class JsonTest {


    public static void main(String[] args) {
        JSONObject jsonObject = readJsonObjectFromFile("D:\\workspace\\test\\a.json");
        JSONArray pageList = jsonObject.getJSONArray("pageList");
        for (Object o : pageList) {
            JSONObject entries = JSONUtil.parseObj(o);
            System.out.println("update mt_deviceinfo set pskey = '" + entries.get("ps_key") + "' where DEVICENO = '" + entries.get("device_sn") + "' and pskey is null;");
        }
    }


    /**
     * 从本地文件读取JSON数据
     * @param filePath 文件路径
     * @return JSON对象 (JSONObject 或 JSONArray)
     */
    public static Object readJsonFromFile(String filePath) {
        // 使用Hutool工具类读取文件内容
        String content = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        
        // 判断是JSONObject还是JSONArray
        if (content.trim().startsWith("[")) {
            // JSONArray
            return JSONUtil.parseArray(content);
        } else {
            // JSONObject
            return JSONUtil.parseObj(content);
        }
    }

    /**
     * 从本地文件读取JSON对象
     * @param filePath 文件路径
     * @return JSONObject
     */
    public static JSONObject readJsonObjectFromFile(String filePath) {
        String content = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        return JSONUtil.parseObj(content);
    }

    /**
     * 从本地文件读取JSON数组
     * @param filePath 文件路径
     * @return JSONArray
     */
    public static JSONArray readJsonArrayFromFile(String filePath) {
        String content = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        return JSONUtil.parseArray(content);
    }
}