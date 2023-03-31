package com.gftraining.microservice_product.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

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
    private BigDecimal price;
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
