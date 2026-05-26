package com.example.design.strategy;

/**
 * 策略模式演示
 *
 * <p>策略模式（Strategy Pattern）属于行为型设计模式，
 * 定义一系列算法，将每个算法封装起来，并使它们可以互相替换。
 * 策略模式让算法独立于使用它的客户端而变化。</p>
 *
 * <p>本示例以支付场景为例：支付宝、微信、银行卡是三种不同的支付策略，
 * 客户端可以在运行时灵活切换支付方式。</p>
 *
 * @author claude
 */
public class StrategyPatternDemo {

    public static void main(String[] args) {
        // 1. 使用支付宝支付
        PayContext context = new PayContext(new AliPayStrategy());
        context.executePayment(99.9);

        // 2. 切换为微信支付
        context.setPayStrategy(new WechatPayStrategy());
        context.executePayment(199.0);

        // 3. 切换为银行卡支付
        context.setPayStrategy(new BankCardPayStrategy());
        context.executePayment(5000.0);
    }
}
