package com.gftraining.microservice_product.controllers;

import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    @GetMapping("/getAll")
    public List<ProductEntity> getAll() {
        return productService.getAll();
    }

    @GetMapping("/name/{name}")
    public List<ProductEntity> getProductByName(@PathVariable String name) {
        return productService.getProductByName(name);
    }

    @GetMapping("/id/{id}")
    public ProductEntity getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/updateStock/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStock(@PathVariable Long id, @RequestBody Integer unitsToSubtract) {
        productService.updateStock(unitsToSubtract, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addProduct(@Valid @RequestBody ProductDTO product) {
        return productService.saveProduct(product);
    }

    @PostMapping("/JSON_load")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProductsFromJson(@RequestParam("path") String path) throws IOException {
            productService.updateProductsFromJson(path);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putProductById(@PathVariable Long id, @Valid @RequestBody ProductDTO newProduct) {
        return productService.putProductById(newProduct, id);
    }
}