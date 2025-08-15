package com.example.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtil {

    // 默认连接超时时间
    private static final int CONNECT_TIMEOUT = 5000;
    // 默认读取超时时间
    private static final int READ_TIMEOUT = 5000;
    // 可选的认证头
    private static String authToken;

    /**
     * 设置全局 Token 认证信息（例如 "Bearer your_token_here"）
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }

    /**
     * 发送 GET 请求
     */
    public static String sendGet(String requestUrl, Map<String, String> headers) throws IOException {
        return sendRequest(requestUrl, "GET", null, headers);
    }

    /**
     * 发送 POST 请求（支持 JSON 数据体）
     */
    public static String sendPost(String requestUrl, String jsonBody, Map<String, String> headers) throws IOException {
        return sendRequest(requestUrl, "POST", jsonBody, headers);
    }

    /**
     * 通用请求方法
     */
    private static String sendRequest(String requestUrl, String method, String jsonBody, Map<String, String> headers) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // 设置基本属性
            conn.setRequestMethod(method);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setUseCaches(false);

            // 设置默认认证头（可选）
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", authToken);
            }

            // 设置用户自定义头部
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 如果是 POST 请求，发送 JSON 数据体
            if ("POST".equalsIgnoreCase(method) && jsonBody != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // 获取响应
            int responseCode = conn.getResponseCode();
            InputStream is;
            if (responseCode >= 200 && responseCode < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } finally {
            conn.disconnect();
        }
    }

    public static void main(String[] args) {
        try {
            String requestUrl = "http://localhost:8366/chargingstation/CsProject/queryPage";
            JSONObject flexi = new JSONObject();
            flexi.put("pageindex", 1);
            flexi.put("pagesize", 10);
            JSONObject search = new JSONObject();
            search.put("notShowCancelProject", true);
            search.put("notRunning", true);
            JSONObject params = new JSONObject();
            params.put("flexi", flexi);
            params.put("search", search);
            String jsonStr = JSONUtil.toJsonStr(params);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("cookie", "PLAY_SESSION=\"b08309bd5e8b6e4dc556e9e550effb1eb175981c-___ID=fae54690-27aa-4ded-ae80-1c35523372f2\"; utockencheckkey=DB93752CDAC1ED90; acw_tc=0bce952217491994364997590ebb483c1f2c6b1cbabc028fda8f8a47d84a30; manageutoken=fc1021eb3c194c07b6a53b72a596bcdc-D8780F; unionsolar_selMenuGroup=8");

            String response = sendPost(requestUrl, jsonStr, headers);
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

