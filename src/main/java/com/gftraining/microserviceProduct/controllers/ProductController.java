package com.gftraining.microserviceProduct.controllers;

import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

private ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
}
    @DeleteMapping("deleteById/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id){
        productService.deleteProductById(id);

}
    @PostMapping(value = "/newProduct")
    public ProductDTO addProduct(@RequestBody ProductDTO product){
        return new ProductDTO(productService.saveProduct(product));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductEntity getProductById(@PathVariable Long id){

        return productService.getProductById(id);
    }
}

