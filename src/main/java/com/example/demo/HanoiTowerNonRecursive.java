package com.example.demo;

import java.util.Stack;

public class HanoiTowerNonRecursive {

    public static void main(String[] args) {
        int numDisks = 3; // 假设有3个盘子
        char source = 'A'; // 起始柱子
        char auxiliary = 'B'; // 辅助柱子
        char target = 'C'; // 目标柱子

        moveDisks(numDisks, source, auxiliary, target);
    }

    public static void moveDisks(int numDisks, char source, char auxiliary, char target) {
        Stack<Character> sourceStack = new Stack<>();
        Stack<Character> auxiliaryStack = new Stack<>();
        Stack<Character> targetStack = new Stack<>();

        // 初始化起始柱子上的盘子
        for (int i = numDisks; i >= 1; i--) {
            sourceStack.push((char) ('A' + i));
        }

        // 循环移动盘子
        while (!sourceStack.isEmpty()) {
            // 将起始柱子上的盘子移动到辅助柱子或目标柱子
            moveDisk(sourceStack, auxiliaryStack, targetStack);

            // 更新source, auxiliary, target柱子
            char temp = source;
            source = auxiliary;
            auxiliary = target;
            target = temp;
        }

        // 打印结果
        System.out.println("Source Tower: " + sourceStack);
        System.out.println("Auxiliary Tower: " + auxiliaryStack);
        System.out.println("Target Tower: " + targetStack);
    }

    private static void moveDisk(Stack<Character> source, Stack<Character> auxiliary, Stack<Character> target) {
        // 取出起始柱子上的最小盘子
        char disk = source.pop();

        // 将辅助柱子上的所有盘子移到目标柱子
        while (!auxiliary.isEmpty() && auxiliary.peek() != disk) {
            Character pop = auxiliary.pop();
            System.out.println("move " + pop + " from auxiliary to target");
            target.push(pop);
        }

        // 将起始柱子上的盘子移到目标柱子
        target.push(disk);
        System.out.println("move " + disk + " from source to target");

        // 将辅助柱子上的剩余盘子移到目标柱子
        while (!auxiliary.isEmpty()) {
            Character pop = auxiliary.pop();
            target.push(pop);
            System.out.println("move " + pop + " from auxiliary to target");
        }
    }
}