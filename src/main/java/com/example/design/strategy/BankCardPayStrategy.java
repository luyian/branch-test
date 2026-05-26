package com.example.design.strategy;

/**
 * 银行卡支付策略
 *
 * @author claude
 */
public class BankCardPayStrategy implements PayStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("【银行卡】正在支付 " + amount + " 元...");
        System.out.println("【银行卡】连接银联通道，发起扣款请求");
        System.out.println("【银行卡】支付成功！");
    }

    @Override
    public String getPayType() {
        return "银行卡";
    }
}
