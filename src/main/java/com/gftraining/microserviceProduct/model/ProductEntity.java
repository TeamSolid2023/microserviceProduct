package com.gftraining.microserviceProduct.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Locale;
@Table(name = "product")
@Entity
@Data
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private CategoryEntity category;
    @JsonProperty
    private String description;
    @JsonProperty
    private double price;
    @JsonProperty
    private Integer stock;



}
