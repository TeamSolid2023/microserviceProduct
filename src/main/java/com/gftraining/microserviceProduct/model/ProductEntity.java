package com.gftraining.microserviceProduct.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

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
    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private CategoryEntity category;
    @NonNull
    @JsonProperty
    private String description;
    @NonNull
    @JsonProperty
    private double price;
    @NonNull
    @JsonProperty
    private Integer stock;

}
