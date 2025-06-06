package com.example.design.builder;

import com.example.demo.House;

public class ConcreteHouseBuilder implements HouseBuilder{
    private House house;

    public ConcreteHouseBuilder() {
        this.house = new House();
    }

    @Override
    public void buildFoundation() {
        house.setFoundation("Built with concrete and steel");
    }

    @Override
    public void buildWalls() {
        house.setWalls("Brick walls with insulation");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Tiled roof with waterproofing");
    }

    @Override
    public House getHouse() {
        return house;
    }
}
