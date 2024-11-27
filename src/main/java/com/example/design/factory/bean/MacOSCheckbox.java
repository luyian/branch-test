package com.example.design.factory.bean;

public class MacOSCheckbox implements CheckBox{
    @Override
    public void paint() {
        System.out.println("MacOS CheckBox");
    }
}
