package com.gftraining.microserviceProduct.services;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
    super();
        this.productRepository = productRepository;
    }

    public List<ProductEntity> allProducts() {
        return productRepository.findAll();
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void putProductById(ProductEntity newProduct, Long id) {
        productRepository.findById(id).map(product ->
        {
            product.setName(newProduct.getName());
            product.setId(id);
            product.setCategory(newProduct.getCategory());
            product.setDescription(newProduct.getDescription());
            product.setPrice(newProduct.getPrice());
            product.setStock(newProduct.getStock());
            return productRepository.save(product);
        });
    }
}
