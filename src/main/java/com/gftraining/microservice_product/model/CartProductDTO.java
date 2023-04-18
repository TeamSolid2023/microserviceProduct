package com.gftraining.microservice_product.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;

}
