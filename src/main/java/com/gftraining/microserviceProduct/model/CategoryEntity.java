package com.gftraining.microserviceProduct.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table(name = "category")
@Entity
@Data
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty
    private String name;
    @JsonProperty
    private Integer discount;

    @OneToMany(mappedBy="category")
    private List<ProductEntity> products;

}
