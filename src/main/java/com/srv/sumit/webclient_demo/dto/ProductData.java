package com.srv.sumit.webclient_demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductData {
    //@JsonProperty("Color")
    private String color;
    private String capacity;

    private int year;
    private double price;

    @JsonProperty("CPU model") // Handle spaces in property names
    private String cpuModel;

    @JsonProperty("Hard disk size") // Handle spaces in property names
    private String hardDiskSize;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getHardDiskSize() {
        return hardDiskSize;
    }

    public void setHardDiskSize(String hardDiskSize) {
        this.hardDiskSize = hardDiskSize;
    }

    // Constructor, toString, equals, hashCode, etc.

    @Override
    public String toString() {
        return "ProductData{" +
                "color='" + color + '\'' +
                ", capacity='" + capacity + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", cpuModel='" + cpuModel + '\'' +
                ", hardDiskSize='" + hardDiskSize + '\'' +
                '}';
    }
}
