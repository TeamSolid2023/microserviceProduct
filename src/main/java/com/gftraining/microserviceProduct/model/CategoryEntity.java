package com.gftraining.microserviceProduct.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntity {
    private String name;
    private int discount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
