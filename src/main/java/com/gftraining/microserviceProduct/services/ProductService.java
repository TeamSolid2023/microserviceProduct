package com.gftraining.microserviceProduct.services;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
import lombok.NonNull;
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

    public @NonNull Long saveProduct(ProductDTO productDTO){
        ProductEntity product = new ProductEntity();

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(productDTO.getCategory());

        return productRepository.save(product).getId();
    }
}
