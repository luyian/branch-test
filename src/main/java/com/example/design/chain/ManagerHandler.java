package com.example.design.chain;

/**
 * 经理审批处理器（可审批 10000 元以下）
 *
 * @author claude
 */
public class ManagerHandler extends ApprovalHandler {

    public ManagerHandler(String name) {
        super(name);
    }

    @Override
    public void handle(double amount) {
        if (amount <= 10000) {
            System.out.println("【经理 " + name + "】审批通过，金额：" + amount + " 元");
        } else {
            System.out.println("【经理 " + name + "】金额 " + amount + " 元超出权限（≤10000），转交上级");
            passToNext(amount);
        }
    }
}
