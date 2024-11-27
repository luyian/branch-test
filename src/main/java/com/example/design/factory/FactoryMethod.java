package com.example.design.factory;

import com.example.design.factory.bean.CircleFactory;
import com.example.design.factory.bean.Shape;
import com.example.design.factory.bean.SquareFactory;

/**
 * 2. 工厂方法模式（Factory Method）
 * 用途
 * 定义一个创建对象的接口，让子类决定实例化哪一个类。
 */
public class FactoryMethod {
    public static void main(String[] args) {
        CircleFactory circleFactory = new CircleFactory();
        Shape circle = circleFactory.getShape();
        circle.draw();

        SquareFactory squareFactory = new SquareFactory();
        Shape square = squareFactory.getShape();
        square.draw();

    }
}
