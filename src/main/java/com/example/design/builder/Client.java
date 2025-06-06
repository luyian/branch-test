package com.example.design.builder;

import com.example.demo.House;

public class Client {
    public static void main(String[] args) {
        HouseBuilder builder = new ConcreteHouseBuilder();
        Director director = new Director(builder);

        director.constructHouse();

        House house = builder.getHouse();
        house.display();
    }
}
