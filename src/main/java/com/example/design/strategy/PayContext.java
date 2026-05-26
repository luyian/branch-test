package com.example.design.strategy;

/**
 * 支付上下文（环境角色）
 *
 * <p>持有策略对象的引用，负责调用具体策略的支付方法。
 * 客户端通过上下文与策略交互，无需关心具体实现。</p>
 *
 * @author claude
 */
public class PayContext {

    private PayStrategy payStrategy;

    public PayContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    /**
     * 切换支付策略
     *
     * @param payStrategy 新的支付策略
     */
    public void setPayStrategy(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    /**
     * 执行支付
     *
     * @param amount 支付金额
     */
    public void executePayment(double amount) {
        System.out.println("========== 使用「" + payStrategy.getPayType() + "」进行支付 ==========");
        payStrategy.pay(amount);
        System.out.println();
    }
}
