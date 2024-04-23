package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;

public class LinkTest {

    public static void main(String[] args) {
        LinkNode lin1 = new LinkNode(1);
        LinkNode lin2 = new LinkNode(2);
        LinkNode lin3 = lin1.setNext(new LinkNode(3));
        LinkNode lin5 = lin3.setNext(new LinkNode(5));
        LinkNode lin4 = lin2.setNext(new LinkNode(4));
        LinkNode lin6 = lin4.setNext(new LinkNode(6));
        lin1.print();

        LinkNode tmp = lin1;
        while (tmp != null) {
            System.out.println(tmp.val);
            tmp = tmp.next;
        }

        tmp = lin2;
        while (tmp != null) {
            System.out.println(tmp.val);
            tmp = tmp.next;
        }

        LinkNode linkNode = merge(lin1, lin2);
        linkNode.print();

//        revert(linkNode);
        System.out.println("=========================");
        LinkNode linkNode1 = revert2(linkNode);
        linkNode1.print();
    }

    /**
     * 特殊情况：如果有一个链表为空，返回另一个链表
     * 如果pHead1 节点值比小pHead2，下一个节点应该是 pHead1，应该return pHead1，在return之前，指定pHead1的下一个节点应该是pHead1.next和pHead2俩链表的合并后的头结点
     * 如果pHead1 节点值比pHead2大，下一个节点应该是pHead2，应该return pHead2，在return之前，指定pHead2的下一个节点应该是pHead1和pHead2.next俩链表的合并后的头结点
     * @param link1
     * @param link2
     * @return
     */
    public static LinkNode merge(LinkNode link1, LinkNode link2) {
        if (link1 == null || link2 == null) {
            return link1 != null ? link1 : link2;
        }

        if (link1.val <= link2.val) {
            link1.next = merge(link1.next, link2);
            return link1;
        } else {
            link2.next = merge(link1, link2.next);
            return link2;
        }
    }

    public static LinkNode revert2(LinkNode head) {
        LinkNode pre = null;
        LinkNode next = null;
        while (head != null) {
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }

    /**
     * 链表反转
     */
    public static void revert(LinkNode linkNode) {
        LinkedList<Integer> list = new LinkedList<>();
        LinkNode tmp = linkNode;
        while (tmp != null) {
            list.push(tmp.val);
            tmp = tmp.next;
        }

        int size = list.size();
        LinkNode temp = new LinkNode(list.pop());
        LinkNode res = temp;
        for (int i = 0; i < size - 1; i++) {
            temp = temp.setNext(new LinkNode(list.pop()));
        }
        res.print();
    }

}

class LinkNode {
    int val;
    LinkNode next = null;

    public LinkNode(int val) {
        this.val = val;
    }

    public LinkNode setNext(LinkNode next) {
        this.next = next;
        return next;
    }

    public void print() {
        StringBuilder sb = new StringBuilder("{");
        LinkNode tmp = this;
        while (tmp != null) {
            if (tmp.next != null) {
                sb.append(tmp.val + ",");
            } else {
                sb.append(tmp.val);
            }
            tmp = tmp.next;
        }

        sb.append("}");
        System.out.println(sb.toString());
    }
}
