package com.gftraining.microservice_product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class ProductDTO {

    @NotBlank(message = "cannot be null.")
    @Pattern(message = "add a name", regexp = "^(?!string$).+$")
    @Schema(example = "string")
    private String name;
    @NotBlank(message = "cannot be null.")
    @Pattern(message = "add a category", regexp = "^(?!string$).+$")
    @Schema(example = "string")
    private String category;
    @NotBlank(message = "cannot be null.")
    @Pattern(message = "add a description", regexp = "^(?!string$).+$")
    @Schema(example = "string")
    private String description;
    @NotNull(message = "cannot be null.")
    @Positive(message = "should be greater than 0.")
    private BigDecimal price;
    @NotNull(message = "cannot be null.")
    @PositiveOrZero(message = "should be positive or 0.")
    private Integer stock;
}
