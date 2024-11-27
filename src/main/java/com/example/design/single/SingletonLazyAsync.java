package com.example.design.single;

/**
 * 3. 懒汉式（线程安全，同步方法）
 * 通过synchronized关键字来保证线程安全，但是每次调用都会同步，影响性能。
 */
public class SingletonLazyAsync {
    private static SingletonLazyAsync instance;

    private SingletonLazyAsync() {}

    public static synchronized SingletonLazyAsync getInstance() {
        if (instance == null) {
            instance = new SingletonLazyAsync();
        }
        return instance;
    }
}
