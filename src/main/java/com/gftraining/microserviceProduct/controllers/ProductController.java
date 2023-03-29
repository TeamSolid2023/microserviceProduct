package com.gftraining.microserviceProduct.controllers;

import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.services.ProductService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gftraining.microserviceProduct.model.ProductEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

private ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    @GetMapping("/getAll")
    public List<ProductEntity> getAll() {
        return productService.allProducts();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id){
        productService.deleteProductById(id);
    }

    @PostMapping(value = "/newProduct")
    public ResponseEntity<Long> addProduct(@RequestBody ProductDTO product){
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductEntity getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public void putProductById(@PathVariable Long id, @RequestBody ProductEntity newProduct) {
        productService.putProductById(newProduct, id);
    }
}

