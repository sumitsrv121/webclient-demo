package com.srv.sumit.webclient_demo.dto;

public class ProductData {

    private String color;
    private String capacity;

    // Getters and Setters
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    // Constructor, toString, equals, hashCode, etc.
    @Override
    public String toString() {
        return "ProductData{" +
                "color='" + color + '\'' +
                ", capacity='" + capacity + '\'' +
                '}';
    }
}
