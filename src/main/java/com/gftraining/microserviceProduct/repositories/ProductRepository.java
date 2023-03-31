package com.gftraining.microserviceProduct.repositories;

import com.gftraining.microserviceProduct.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    List<ProductEntity> findAllByName(String name);
    ;
}
