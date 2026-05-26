package com.example.design.strategy;

/**
 * 支付策略接口
 *
 * <p>定义所有支付方式的统一行为规范</p>
 *
 * @author claude
 */
public interface PayStrategy {

    /**
     * 执行支付
     *
     * @param amount 支付金额（单位：元）
     */
    void pay(double amount);

    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    String getPayType();
}
