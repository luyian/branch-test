package com.example.funtion;

public class FunctionTest {

    public static void main(String[] args) {
        String params = "asfg";
        String res = doRun(() -> queryPage(params));
    }

    static String doRun(Demo demo) {
        return demo.run();
    }

    static String queryPage(String str) {
        return str + "query page";
    }

    interface Demo {
        String run();
    }

}

