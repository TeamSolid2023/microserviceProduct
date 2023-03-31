package com.gftraining.microserviceProduct.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "product")
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductEntity {

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
