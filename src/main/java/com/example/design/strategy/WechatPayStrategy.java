package com.example.design.strategy;

/**
 * 微信支付策略
 *
 * @author claude
 */
public class WechatPayStrategy implements PayStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("【微信支付】正在支付 " + amount + " 元...");
        System.out.println("【微信支付】调用微信SDK，唤起微信支付");
        System.out.println("【微信支付】支付成功！");
    }

    @Override
    public String getPayType() {
        return "微信支付";
    }
}
