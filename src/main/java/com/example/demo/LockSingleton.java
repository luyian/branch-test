package com.example.demo;

import org.junit.jupiter.api.Test;

/**
 * 双重锁校验单例
 */
public class LockSingleton {

    private volatile static LockSingleton lockSingleton;
    private LockSingleton() {}

    public LockSingleton getLockSingleton() {
        if (lockSingleton == null) {
            synchronized (LockSingleton.class) {
                if (lockSingleton == null) {
                    lockSingleton = new LockSingleton();
                }
            }
        }
        return lockSingleton;
    }

    @Test
    public void test01() {
        System.out.println(getSum(7));
    }

    public int getSum(int n) {
        if (n <= 0) {
            return 0;
        } else {
            return n + getSum(n-1);
        }
    }

    @Test
    public void test02() {
        E d = new E();
        System.out.println(d.str);
    }
}

interface A {
    String str = "asf";
    void showStr();
}
interface B extends A {
    String STR = "bcd";
}

class C implements B {

    @Override
    public void showStr() {
        System.out.println(this.str);
    }
}
abstract class D {
    protected String str;
}
class E extends D {

}