package com.gftraining.microserviceProduct.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductDTO {

    @NotNull
    private String name;
    @NonNull
    private CategoryEntity category;
    @NonNull
    private String description;
    @NonNull
    private double price;
    @NonNull
    private Integer stock;

    public ProductDTO(ProductEntity product) {
        this.name = product.getName();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
    }
}
