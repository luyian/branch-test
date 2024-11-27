package com.example.design.factory.bean;

public class WindowsButton implements Button{
    @Override
    public void paint() {
        System.out.println("Windows Button");
    }
}
