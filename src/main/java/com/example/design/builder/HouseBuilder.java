package com.example.design.builder;

import com.example.demo.House;

public interface HouseBuilder {
    void buildFoundation();
    void buildWalls();
    void buildRoof();
    House getHouse();
}
