package com.gftraining.microserviceProduct.repositories;

import com.gftraining.microserviceProduct.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    ProductEntity findByName(String name);
}
