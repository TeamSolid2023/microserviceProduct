package com.gftraining.microserviceProduct.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.List;

@Table(name = "category")
@Entity
@Data
@NoArgsConstructor
public class CategoryEntity {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @JsonProperty
    private String name;
    @NonNull
    @JsonProperty
    private Integer discount;

    @JsonIgnore
    @OneToMany(mappedBy="category")
    private List<ProductEntity> products;

}
