package com.example.design.factory.bean;

public class WindowsCheckbox implements CheckBox{
    @Override
    public void paint() {
        System.out.println("Windows CheckBox");
    }
}
