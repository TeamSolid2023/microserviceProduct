package com.gftraining.microservice_product.controllers;


import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        return productService.getAll();
    }

    @GetMapping("/id/{id}")
    public ProductEntity getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @GetMapping("/name/{name}")
    public List<ProductEntity> getProductByName(@PathVariable String name) {
        return productService.getProductByName(name);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id){
        productService.deleteProductById(id);
    }

    @PutMapping("/{id}")
    public void putProductById(@PathVariable Long id, @RequestBody ProductDTO newProduct) {
        productService.putProductById(newProduct, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> addProduct(@Valid @RequestBody ProductDTO product){
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @PostMapping("/JSON_load")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProductsFromJson(@RequestParam("path") String path) throws IOException {
            productService.updateProductsFromJson(path);
    }
}
