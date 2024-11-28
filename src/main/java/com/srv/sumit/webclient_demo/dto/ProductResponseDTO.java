package com.srv.sumit.webclient_demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ProductResponseDTO {

    private String id;
    private String name;
    private ProductDataDTO data;
    
    @JsonProperty("createdAt")
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt; // Use LocalDateTime for proper date handling

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
