package com.srv.sumit.webclient_demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductRequestDTO {

    private String name;

    private ProductDataDTO data;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductDataDTO getData() {
        return data;
    }

    public void setData(ProductDataDTO data) {
        this.data = data;
    }

    // Inner class for the "data" field
    public static class ProductDataDTO {
        private int year;
        private double price;
        @JsonProperty("CPU model") // Handle spaces in property names
        private String cpuModel;
        @JsonProperty("Hard disk size") // Handle spaces in property names
        private String hardDiskSize;

        // Getters and Setters
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
    }
}
