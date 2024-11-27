package com.example.design.single;

/**
 * 1. 饿汉式（静态常量）
 * 这种方式在类加载时就完成了初始化，避免了线程同步问题，不过在类初始化时就完成了创建，浪费了资源。
 */
public class Singleton {
    // 创建 Singleton 的一个对象
    private static final Singleton instance = new Singleton();

    // 私有化构造方法
    private Singleton() {}

    // 获取唯一可用的对象
    public static Singleton getInstance() {
        return instance;
    }
}
