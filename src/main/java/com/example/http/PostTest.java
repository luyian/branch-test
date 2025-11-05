package com.example.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class PostTest {

    public static void main(String[] args) throws Exception {
        String url = "https://ahgs.cnnp.com.cn/monitorserver/api-out/outerpower/anyCatchDeviceData";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("utoken", "7AB615619ED420D3DB32DCFA54FF1066");
        headers.put("cookie", "PLAY_SESSION=\"6efcb3c99df41b9d04811020d7c2395c899947b8-___ID=1465cbb8-a177-49b4-8e92-f66efccb6938\"; manageutoken=7AB615619ED420D3DB32DCFA54FF1066-6E0A6C; unionsolar_selMenuGroup=4");

        // 设置起始日期和结束日期
        String startDateStr = "2025-07-16";
        String endDateStr = "2025-10-20";

        // sn码
        String sns = "A2462608492,A2462715047,A2340309182,A2462608510";
        String[] snArr = Arrays.stream(sns.split(",")).distinct().toArray(String[]::new);
        for (int i = 0; i < snArr.length; i++) {
            doPostRequest(headers, url, snArr[i]);
            // 添加进度条

            System.out.print("当前进度 =======> " + (i+1) + " / " + snArr.length);
        }
    }


    public static void doPostRequest(Map<String, String> headers, String url, String sn) throws Exception{
        // 设置起始日期和结束日期
        String startDateStr = "2025-07-16";
        String endDateStr = "2025-10-20";
        // 解析日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.println("开始处理 ====> " + sn);
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // 循环处理29天的时间间隔
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            while (!calendar.getTime().after(endDate)) {
                // 计算当前周期的开始和结束日期
                Date cycleStart = calendar.getTime();
                Calendar endCalendar = (Calendar) calendar.clone();
                endCalendar.add(Calendar.DAY_OF_MONTH, 0); // 加28天，总共29天

                // 确保结束日期不超过总结束日期
                if (endCalendar.getTime().after(endDate)) {
                    endCalendar.setTime(endDate);
                }

                Date cycleEnd = endCalendar.getTime();

                // 格式化日期
                String cycleStartStr = sdf.format(cycleStart);
                String cycleEndStr = sdf.format(cycleEnd);

                System.out.println("正在处理周期: " + cycleStartStr + " 到 " + cycleEndStr);

                // 设置参数
                Map<String, String> params = new HashMap<>();
                params.put("deviceSn", sn);
                params.put("startData", cycleStartStr);
                params.put("endDate", cycleEndStr);

                // 将参数转换为JSON格式的请求体
                String body = JSONUtil.toJsonStr(params);

                // 发送请求
                System.out.println("请求体: " + body);
                String response = sendPostRequest(url, headers, body);
                System.out.println("响应结果: " + response);

                // 移动到下一个周期
                calendar.add(Calendar.DAY_OF_MONTH, 1); // 移动到下一周期
                // 添加延迟以避免请求过于频繁
                Thread.sleep(2000);
            }
            Thread.sleep(60000*28);
            System.out.println("处理完成 ====> " + sn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送HTTP请求，可以自定义header和body
     *
     * @param url     请求地址
     * @param headers 请求头
     * @param body    请求体
     * @return 响应内容
     */
    public static String sendPostRequest(String url, Map<String, String> headers, String body) {
        try {
            // 创建HTTP请求
            HttpRequest request = HttpRequest.post(url);
            
            // 添加请求头
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(request::header);
            }
            
            // 添加请求体
            if (body != null && !body.isEmpty()) {
                request.body(body);
            }
            
            // 发送请求并获取响应
            HttpResponse response = request.execute();
            
            // 返回响应内容
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}