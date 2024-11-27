package com.example.design.factory.bean;

public class CircleFactory implements FactoryMethodInterface {
    @Override
    public Shape getShape() {
        return new Circle();
    }
}
