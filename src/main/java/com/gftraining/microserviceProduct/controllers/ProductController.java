package com.gftraining.microserviceProduct.controllers;


import com.gftraining.microserviceProduct.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
