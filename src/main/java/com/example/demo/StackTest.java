package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StackTest {

    @Test
    public void test01() {
        String str = "([]({[}){})";
        Map<Character, Character> keyMap = new HashMap<>();
        keyMap.put('}', '{');
        keyMap.put(')', '(');
        keyMap.put(']', '[');

        LinkedList<Character> list1 = new LinkedList<>();
        LinkedList<Character> list2 = new LinkedList<>();

        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(' || chars[i] == '[' || chars[i] == '{') {
                list1.push(chars[i]);
            } else {
                list2.push(chars[i]);
                while (list1.size() > 0 && keyMap.get(list2.peek()) == list1.peek()) {
                    list1.pop();
                    list2.pop();
                }
            }
        }

        if (list1.size() == 0 && list2.size() == 0) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }

    }
}
