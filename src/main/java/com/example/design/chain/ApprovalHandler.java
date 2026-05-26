package com.example.design.chain;

/**
 * 审批处理器（抽象处理者）
 *
 * <p>定义审批链的通用行为：持有下一个处理者的引用，
 * 若自身无法处理则传递给下一个处理者。</p>
 *
 * @author claude
 */
public abstract class ApprovalHandler {

    /** 处理者名称 */
    protected String name;

    /** 下一个处理者 */
    protected ApprovalHandler nextHandler;

    public ApprovalHandler(String name) {
        this.name = name;
    }

    /**
     * 设置下一个处理者，返回下一个处理者以支持链式调用
     *
     * @param nextHandler 下一个处理者
     * @return 下一个处理者
     */
    public ApprovalHandler setNext(ApprovalHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    /**
     * 处理审批请求
     *
     * @param amount 请求金额（单位：元）
     */
    public abstract void handle(double amount);

    /**
     * 将请求传递给下一个处理者
     *
     * @param amount 请求金额
     */
    protected void passToNext(double amount) {
        if (nextHandler != null) {
            nextHandler.handle(amount);
        } else {
            System.out.println("【审批终止】没有更高级别的审批人，金额 " + amount + " 元的请求被驳回");
        }
    }
}
