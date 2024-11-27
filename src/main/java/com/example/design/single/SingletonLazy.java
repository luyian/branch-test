package com.example.design.single;

/**
 * 2. 懒汉式（线程不安全）
 * 这种方式是最基本的实现，但在多线程环境下可能会出现问题。
 */
public class SingletonLazy {
    private static SingletonLazy instance;

    private SingletonLazy() {}

    public static SingletonLazy getInstance() {
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}
