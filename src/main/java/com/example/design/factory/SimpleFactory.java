package com.example.design.factory;

import com.example.design.factory.bean.Circle;
import com.example.design.factory.bean.Shape;
import com.example.design.factory.bean.Square;

/**
 * 1. 简单工厂模式（Simple Factory）
 * 用途
 * 提供一个静态方法来创建对象，隐藏创建逻辑。
 */
public class SimpleFactory {
    public static Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        return null;
    }

    public static void main(String[] args) {
        Shape shape = SimpleFactory.getShape("CIRCLE");
        shape.draw();
    }
}


