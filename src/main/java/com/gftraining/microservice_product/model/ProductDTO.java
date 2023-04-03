package com.gftraining.microservice_product.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductDTO {

    @NotNull(message = "name cannot be null")
    @NonNull private String name;
    @NotNull(message = "category cannot be null")
    @NonNull private String category;
    @NotNull(message = "description cannot be null")
    @NonNull private String description;
    @NotNull(message = "price cannot be null")
    @NonNull private BigDecimal price;
    @NotNull(message = "stock cannot be null")
    @NonNull private Integer stock;

    public ProductDTO(ProductEntity product) {
        this.name = product.getName();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
    }
}
