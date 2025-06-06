package com.example.demo;

// 最终构建的复杂对象
public class House {
    private String foundation;
    private String walls;
    private String roof;

    // 用于展示构建结果
    public void display() {
        System.out.println("Foundation: " + foundation);
        System.out.println("Walls: " + walls);
        System.out.println("Roof: " + roof);
    }

    // setter 方法（或通过 Builder 内部类赋值）
    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public void setWalls(String walls) {
        this.walls = walls;
    }

    public void setRoof(String roof) {
        this.roof = roof;
    }
}
