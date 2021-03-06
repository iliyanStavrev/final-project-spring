package com.example.sportclopedia.model.dto;

import java.time.LocalDateTime;

public class HallDto {

    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    private LocalDateTime availableOn;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getAvailableOn() {
        return availableOn;
    }

    public void setAvailableOn(LocalDateTime availableOn) {
        this.availableOn = availableOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
