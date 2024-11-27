package com.example.design.single;

/**
 * 5. 枚举
 * 利用枚举的特性来实现单例，简洁且保证线程安全。
 */
public enum SingletonEnum {
    INSTANCE;

    public void someMethod() {
        // 实现业务逻辑
    }
}
