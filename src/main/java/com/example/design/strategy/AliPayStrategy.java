package com.example.design.strategy;

/**
 * 支付宝支付策略
 *
 * @author claude
 */
public class AliPayStrategy implements PayStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("【支付宝】正在支付 " + amount + " 元...");
        System.out.println("【支付宝】调用支付宝SDK，生成支付二维码");
        System.out.println("【支付宝】支付成功！");
    }

    @Override
    public String getPayType() {
        return "支付宝";
    }
}
