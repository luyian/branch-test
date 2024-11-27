package com.example.design.single;

/**
 * 4. 双重检查锁定
 * 这是一种较为高效的懒汉式实现方式，只在必要时才同步。
 */
public class SingletonLazyDoubleLock {
    private volatile static SingletonLazyDoubleLock instance;

    private SingletonLazyDoubleLock() {}

    public static SingletonLazyDoubleLock getInstance() {
        if (instance == null) {
            synchronized (SingletonLazyDoubleLock.class) {
                if (instance == null) {
                    instance = new SingletonLazyDoubleLock();
                }
            }
        }
        return instance;
    }
}
