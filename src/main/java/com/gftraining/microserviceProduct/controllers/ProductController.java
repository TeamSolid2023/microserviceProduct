package com.gftraining.microserviceProduct.controllers;

import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.gftraining.microserviceProduct.model.ProductEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.util.*;

import java.io.IOException;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> addProduct(@RequestBody ProductDTO product){
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ProductEntity getProductById(@PathVariable Long id){
        return Optional.ofNullable(productService.getProductById(id))
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
    }

    @GetMapping("/name/{name}")
    public ProductEntity getProductByName(@PathVariable String name) {
        return Optional.ofNullable(productService.getProductByName(name))
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @PostMapping("/JSON_load")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProductsFromJson() throws IOException {
            productService.updateProductsFromJson();
    }
}

