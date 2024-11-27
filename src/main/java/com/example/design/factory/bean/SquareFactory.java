package com.example.design.factory.bean;

public class SquareFactory implements FactoryMethodInterface {
    @Override
    public Shape getShape() {
        return new Square();
    }
}
