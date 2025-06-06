package com.example.design.decprator;

public class Client {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println("Description: " + coffee.description());

        Coffee milkCoffee = new MilkDecorator(coffee);
        System.out.println("Description: " + milkCoffee.description());

        Coffee sugarAndMilkCoffee = new SugarDecorator(milkCoffee);
        System.out.println("Description: " + sugarAndMilkCoffee.description());

    }
}
