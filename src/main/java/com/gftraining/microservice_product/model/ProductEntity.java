package com.gftraining.microservice_product.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "product")
@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ProductEntity implements Serializable {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @JsonProperty
    private String name;
    @NonNull
    @JsonProperty
    private String category;
    @NonNull
    @JsonProperty
    private String description;
    @NonNull
    @JsonProperty
    private BigDecimal price;
    @NonNull
    @JsonProperty
    private Integer stock;
    @Transient
    @JsonProperty
    private BigDecimal finalPrice;
}
