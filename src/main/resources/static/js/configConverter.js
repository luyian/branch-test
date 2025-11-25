/**
 * 配置项转换工具类
 */

class ConfigConverter {
    /**
     * 根据用户选择的项目创建SysConfig列表
     * @param {string} value1 电压相关的选择项
     * @param {string} value2 流量相关的选择项
     * @param {string} value3 功率相关的选择项
     * @returns {Array} 封装好的SysConfig数组
     */
    static createSysConfigs(value1, value2, value3) {
        let configs = [];
        
        // 解析输入值，存储在集合中便于查找
        const voltageSet = this.parseValues(value1);
        const flowSet = this.parseValues(value2);
        const powerSet = this.parseValues(value3);
        
        // 按照 voltage > flow > power 的顺序生成配置项
        
        // 先生成所有 voltage 配置 (标识为1)
        for (let i = 1; i <= 20; i++) {
            configs.push({
                property: "sc" + i + "voltage",
                name: "组串" + i,
                value: "1" + i.toString().padStart(2, '0'), // voltage标识为1，后面跟两位索引
                checked: voltageSet.has(i)
            });
        }
        
        // 再生成所有 flow 配置 (标识为2)
        for (let i = 1; i <= 20; i++) {
            configs.push({
                property: "sc" + i + "flow",
                name: "组串" + i,
                value: "2" + i.toString().padStart(2, '0'), // flow标识为2，后面跟两位索引
                checked: flowSet.has(i)
            });
        }
        
        // 最后生成所有 power 配置 (标识为3)
        for (let i = 1; i <= 20; i++) {
            configs.push({
                property: "sc" + i + "power",
                name: "组串" + i,
                value: "3" + i.toString().padStart(2, '0'), // power标识为3，后面跟两位索引
                checked: powerSet.has(i)
            });
        }
        
        return configs;
    }
    
    /**
     * 将SysConfig列表逆向转换为value1、value2、value3字符串数组
     * @param {Array} configs SysConfig数组
     * @returns {Array} 包含value1、value2、value3的字符串数组
     */
    static reverseFromSysConfigs(configs) {
        let value1 = [];
        let value2 = [];
        let value3 = [];
        
        configs.forEach(config => {
            if (config.checked) {
                const value = config.value;
                if (value && value.length === 3) {
                    const type = value.charAt(0);
                    const index = value.substring(1);
                    
                    // 根据第一位标识位判断类型
                    switch (type) {
                        case '1': // voltage
                            value1.push(index);
                            break;
                        case '2': // flow
                            value2.push(index);
                            break;
                        case '3': // power
                            value3.push(index);
                            break;
                    }
                }
            }
        });
        
        return [
            value1.join(", "),
            value2.join(", "),
            value3.join(", ")
        ];
    }
    
    /**
     * 解析逗号分隔的字符串值并转换为Set集合
     * @param {string} value 逗号分隔的字符串
     * @returns {Set} 数字集合
     */
    static parseValues(value) {
        const valueSet = new Set();
        if (value && value.trim() !== "") {
            const items = value.split(",");
            items.forEach(item => {
                const trimmedItem = item.trim();
                if (trimmedItem !== "") {
                    const index = parseInt(trimmedItem, 10);
                    if (!isNaN(index)) {
                        valueSet.add(index);
                    }
                }
            });
        }
        return valueSet;
    }
}

// 测试代码
function runTests() {
    console.log("=== 基本功能测试 ===");
    const value1 = "01, 02";
    const value2 = "01, 02";
    const value3 = "01, 02";
    
    const configs = ConfigConverter.createSysConfigs(value1, value2, value3);
    console.log("生成的配置项数量:", configs.length);
    
    configs.forEach((config, index) => {
        if (index < 5 || index > configs.length - 6) { // 只打印前5个和后5个
            console.log(`Property: ${config.property}, Name: ${config.name}, Value: ${config.value}, Checked: ${config.checked}`);
        } else if (index === 5) {
            console.log("...");
        }
    });
    
    const reversedValues = ConfigConverter.reverseFromSysConfigs(configs);
    console.log("逆向转换结果:");
    console.log("value1:", reversedValues[0]);
    console.log("value2:", reversedValues[1]);
    console.log("value3:", reversedValues[2]);
    
    console.log("\n=== 空值测试 ===");
    const emptyConfigs = ConfigConverter.createSysConfigs("", "", "");
    const emptyReversedValues = ConfigConverter.reverseFromSysConfigs(emptyConfigs);
    console.log("空值逆向转换结果:");
    console.log("value1:", `'${emptyReversedValues[0]}'`);
    console.log("value2:", `'${emptyReversedValues[1]}'`);
    console.log("value3:", `'${emptyReversedValues[2]}'`);
    
    console.log("\n=== 一致性校验测试 ===");
    const testValue1 = "01, 03, 05";
    const testValue2 = "02, 04";
    const testValue3 = "06, 10, 15";
    
    console.log("原始值:");
    console.log("value1:", testValue1);
    console.log("value2:", testValue2);
    console.log("value3:", testValue3);
    
    const testConfigs = ConfigConverter.createSysConfigs(testValue1, testValue2, testValue3);
    const testReversedValues = ConfigConverter.reverseFromSysConfigs(testConfigs);
    
    console.log("逆向转换结果:");
    console.log("value1:", testReversedValues[0]);
    console.log("value2:", testReversedValues[1]);
    console.log("value3:", testReversedValues[2]);
    
    const value1Match = testValue1 === testReversedValues[0];
    const value2Match = testValue2 === testReversedValues[1];
    const value3Match = testValue3 === testReversedValues[2];
    
    console.log("一致性校验结果:");
    console.log("value1 匹配:", value1Match);
    console.log("value2 匹配:", value2Match);
    console.log("value3 匹配:", value3Match);
    console.log("整体一致性:", value1Match && value2Match && value3Match);
}

// 如果在浏览器环境中运行，绑定到全局对象
if (typeof window !== 'undefined') {
    window.ConfigConverter = ConfigConverter;
}

// 如果在Node.js环境中运行，导出模块
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ConfigConverter;
}

// 运行测试
runTests();