package com.example.test;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringTest {

    @Test
    public void test1() {
        String value1 = "01, 02";
        String value2 = "01, 02, 13, 20, 21";
        String value3 = "01, 02";

        List<SysMonitorHistoryColumnConfig> configs = createSysMonitorHistoryColumnConfigs(value1, value2, value3);
        System.out.println(JSONUtil.toJsonStr(configs));
        configs.forEach(config -> System.out.println("Property: " + config.getProperty() + ", Name: " + config.getName() + ", Value: " + config.getValue() + ", Checked: " + config.isChecked()));
        
        // 测试逆向转换方法
        String[] values = reverseFromSysMonitorHistoryColumnConfigs(configs);
        System.out.println("Reversed value1: " + values[0]);
        System.out.println("Reversed value2: " + values[1]);
        System.out.println("Reversed value3: " + values[2]);
    }
    
    @Test
    public void testEmptyValues() {
        System.out.println("\n测试空值情况:");
        List<SysMonitorHistoryColumnConfig> emptyConfigs = createSysMonitorHistoryColumnConfigs("", "", "");
        String[] emptyValues = reverseFromSysMonitorHistoryColumnConfigs(emptyConfigs);
        System.out.println("Empty value1: '" + emptyValues[0] + "'");
        System.out.println("Empty value2: '" + emptyValues[1] + "'");
        System.out.println("Empty value3: '" + emptyValues[2] + "'");
    }
    
    @Test
    public void testForwardAndReverse() {
        System.out.println("\n正向和逆向过程校验测试:");
        String value1 = "01, 03, 05";
        String value2 = "02, 04";
        String value3 = "06, 10, 15";
        
        // 正向过程
        List<SysMonitorHistoryColumnConfig> configs = createSysMonitorHistoryColumnConfigs(value1, value2, value3);
        
        // 逆向过程
        String[] reversedValues = reverseFromSysMonitorHistoryColumnConfigs(configs);

        System.out.println("Original value1: " + value1);
        System.out.println("Original value2: " + value2);
        System.out.println("Original value3: " + value3);
        System.out.println("Reversed value1: " + reversedValues[0]);
        System.out.println("Reversed value2: " + reversedValues[1]);
        System.out.println("Reversed value3: " + reversedValues[2]);
        
        // 验证正反向一致性
        boolean value1Match = value1.equals(reversedValues[0]);
        boolean value2Match = value2.equals(reversedValues[1]);
        boolean value3Match = value3.equals(reversedValues[2]);
        
        System.out.println("Value1 match: " + value1Match);
        System.out.println("Value2 match: " + value2Match);
        System.out.println("Value3 match: " + value3Match);
        System.out.println("Forward and reverse process validation: " + (value1Match && value2Match && value3Match));
    }

    /**
     * 根据用户选择的项目创建SysMonitorHistoryColumnConfig列表
     * @param value1 电压相关的选择项
     * @param value2 流量相关的选择项
     * @param value3 功率相关的选择项
     * @return 封装好的SysMonitorHistoryColumnConfig列表
     */
    public List<SysMonitorHistoryColumnConfig> createSysMonitorHistoryColumnConfigs(String value1, String value2, String value3) {
        List<SysMonitorHistoryColumnConfig> configs = new ArrayList<>();
        
        // 解析输入值，存储在集合中便于查找
        Set<Integer> voltageSet = parseValues(value1);
        Set<Integer> flowSet = parseValues(value2);
        Set<Integer> powerSet = parseValues(value3);
        
        // 按照 voltage > flow > power 的顺序生成配置项
        
        // 先生成所有 voltage 配置
        for (int i = 1; i <= 20; i++) {
            SysMonitorHistoryColumnConfig voltageConfig = new SysMonitorHistoryColumnConfig();
            voltageConfig.setProperty("sc" + i + "voltage");
            voltageConfig.setName("组串" + i);
            voltageConfig.setValue("1" + String.format("%02d", i)); // voltage标识为1，后面跟两位索引
            voltageConfig.setChecked(voltageSet.contains(i));
            configs.add(voltageConfig);
        }
        
        // 再生成所有 flow 配置
        for (int i = 1; i <= 20; i++) {
            SysMonitorHistoryColumnConfig flowConfig = new SysMonitorHistoryColumnConfig();
            flowConfig.setProperty("sc" + i + "flow");
            flowConfig.setName("组串" + i);
            flowConfig.setValue("2" + String.format("%02d", i)); // flow标识为2，后面跟两位索引
            flowConfig.setChecked(flowSet.contains(i));
            configs.add(flowConfig);
        }
        
        // 最后生成所有 power 配置
        for (int i = 1; i <= 20; i++) {
            SysMonitorHistoryColumnConfig powerConfig = new SysMonitorHistoryColumnConfig();
            powerConfig.setProperty("sc" + i + "power");
            powerConfig.setName("组串" + i);
            powerConfig.setValue("3" + String.format("%02d", i)); // power标识为3，后面跟两位索引
            powerConfig.setChecked(powerSet.contains(i));
            configs.add(powerConfig);
        }
        
        return configs;
    }
    
    /**
     * 将SysMonitorHistoryColumnConfig列表逆向转换为value1、value2、value3字符串数组
     * @param configs SysMonitorHistoryColumnConfig列表
     * @return 包含value1、value2、value3的字符串数组
     */
    public String[] reverseFromSysMonitorHistoryColumnConfigs(List<SysMonitorHistoryColumnConfig> configs) {
        StringBuilder value1 = new StringBuilder();
        StringBuilder value2 = new StringBuilder();
        StringBuilder value3 = new StringBuilder();
        
        for (SysMonitorHistoryColumnConfig config : configs) {
            if (config.isChecked()) {
                String value = config.getValue();
                if (value != null && value.length() == 3) {
                    char type = value.charAt(0);
                    String index = value.substring(1);
                    
                    // 根据第一位标识位判断类型
                    switch (type) {
                        case '1': // voltage
                            if (value1.length() > 0) {
                                value1.append(", ");
                            }
                            value1.append(index);
                            break;
                        case '2': // flow
                            if (value2.length() > 0) {
                                value2.append(", ");
                            }
                            value2.append(index);
                            break;
                        case '3': // power
                            if (value3.length() > 0) {
                                value3.append(", ");
                            }
                            value3.append(index);
                            break;
                    }
                }
            }
        }
        
        return new String[] {value1.toString(), value2.toString(), value3.toString()};
    }
    
    /**
     * 解析逗号分隔的字符串值并转换为整数集合
     * @param value 逗号分隔的字符串
     * @return 整数集合
     */
    private Set<Integer> parseValues(String value) {
        Set<Integer> valueSet = new HashSet<>();
        if (value != null && !value.isEmpty()) {
            String[] items = value.split(",");
            for (String item : items) {
                String trimmedItem = item.trim();
                if (!trimmedItem.isEmpty()) {
                    try {
                        int index = Integer.parseInt(trimmedItem);
                        valueSet.add(index);
                    } catch (NumberFormatException e) {
                        // 忽略无效格式
                    }
                }
            }
        }
        return valueSet;
    }



    @Data
    class SysMonitorHistoryColumnConfig {
        private String property;
        private String name;
        private String value;
        private boolean checked;
    }


}