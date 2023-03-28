package com.gftraining.microserviceProduct.services;

import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
    super();
        this.productRepository = productRepository;}
    public void deleteProductById(Long id) {

        productRepository.deleteById(id);
    }
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

}
