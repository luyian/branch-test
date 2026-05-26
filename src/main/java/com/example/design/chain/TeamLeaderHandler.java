package com.example.design.chain;

/**
 * 组长审批处理器（可审批 1000 元以下）
 *
 * @author claude
 */
public class TeamLeaderHandler extends ApprovalHandler {

    public TeamLeaderHandler(String name) {
        super(name);
    }

    @Override
    public void handle(double amount) {
        if (amount <= 1000) {
            System.out.println("【组长 " + name + "】审批通过，金额：" + amount + " 元");
        } else {
            System.out.println("【组长 " + name + "】金额 " + amount + " 元超出权限（≤1000），转交上级");
            passToNext(amount);
        }
    }
}
