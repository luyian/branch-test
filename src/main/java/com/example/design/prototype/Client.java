package com.example.design.prototype;

public class Client {
    public static void main(String[] args) {
        ConcretePrototype prototype = new ConcretePrototype("Original", 100);

        // 克隆对象
        ConcretePrototype clone = prototype.clone();

        System.out.println("Original: " + prototype);
        System.out.println("Clone: " + clone);
    }
}
