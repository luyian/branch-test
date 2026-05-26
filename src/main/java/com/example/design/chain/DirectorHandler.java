package com.example.design.chain;

/**
 * 总监审批处理器（可审批 50000 元以下）
 *
 * @author claude
 */
public class DirectorHandler extends ApprovalHandler {

    public DirectorHandler(String name) {
        super(name);
    }

    @Override
    public void handle(double amount) {
        if (amount <= 50000) {
            System.out.println("【总监 " + name + "】审批通过，金额：" + amount + " 元");
        } else {
            System.out.println("【总监 " + name + "】金额 " + amount + " 元超出权限（≤50000），转交上级");
            passToNext(amount);
        }
    }
}
