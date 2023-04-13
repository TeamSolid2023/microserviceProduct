package com.gftraining.microservice_product.controllers;


import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.model.ResponseHandler;
import com.gftraining.microservice_product.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addProduct(@Valid @RequestBody ProductDTO product){
        try {
            Long id = productService.saveProduct(product);
            return ResponseHandler.generateResponse("DDBB updated",HttpStatus.CREATED,id);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/JSON_load")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProductsFromJson(@RequestParam("path") String path) throws IOException {
            productService.updateProductsFromJson(path);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putProductById(@PathVariable Long id, @Valid @RequestBody ProductDTO newProduct) {
        try {
            productService.putProductById(newProduct, id);

            return ResponseHandler.generateResponse("DDBB updated",HttpStatus.CREATED,id);

        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/updateStock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStock(@PathVariable Long id, @RequestBody Integer units) {
        productService.updateStock(units, id);
    }
}
