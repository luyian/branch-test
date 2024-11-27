package com.example.design.factory.bean;

public class MacOSButton implements Button{
    @Override
    public void paint() {
        System.out.println("MacOS Button");
    }
}
