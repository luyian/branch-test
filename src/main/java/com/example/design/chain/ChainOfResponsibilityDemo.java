package com.example.design.chain;

/**
 * 责任链模式演示
 *
 * <p>责任链模式（Chain of Responsibility Pattern）属于行为型设计模式，
 * 使多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系。
 * 将这些对象连成一条链，沿着这条链传递请求，直到有一个对象处理它为止。</p>
 *
 * <p>本示例以费用审批为场景：组长、经理、总监构成审批链，
 * 不同金额的审批请求会沿链传递，直到找到有权限的审批人。</p>
 *
 * @author claude
 */
public class ChainOfResponsibilityDemo {

    public static void main(String[] args) {
        // 构建审批链：组长 -> 经理 -> 总监
        ApprovalHandler teamLeader = new TeamLeaderHandler("张三");
        ApprovalHandler manager = new ManagerHandler("李四");
        ApprovalHandler director = new DirectorHandler("王五");
        teamLeader.setNext(manager).setNext(director);

        // 测试不同金额的审批请求
        System.out.println("===== 请求审批 500 元 =====");
        teamLeader.handle(500);

        System.out.println();
        System.out.println("===== 请求审批 5000 元 =====");
        teamLeader.handle(5000);

        System.out.println();
        System.out.println("===== 请求审批 30000 元 =====");
        teamLeader.handle(30000);

        System.out.println();
        System.out.println("===== 请求审批 100000 元 =====");
        teamLeader.handle(100000);
    }
}
