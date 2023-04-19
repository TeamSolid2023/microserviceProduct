package com.gftraining.microservice_product.controllers;


import com.gftraining.microservice_product.configuration.FeatureFlagsConfig;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.model.ResponseHandler;
import com.gftraining.microservice_product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
@Slf4j
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private ProductService productService;
    private FeatureFlagsConfig featureFlag;

    public ProductController(ProductService productService, FeatureFlagsConfig microserviceStatus) {
        super();
        this.productService = productService;
        this.featureFlag = microserviceStatus;
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);

        String message = "Product with id " + id + " deleted successfully.";

        if (featureFlag.isCallCartEnabled()) {
            log.info("Feature flag to call CART is ENABLED");
            productService.deleteCartProducts(id).subscribe(result -> log.info("Respuesta delete product from carrito: " + result.toString()));
        } else {
            log.info("Feature flag to call CART is DISABLED");
            message = message + " Feature flag to call CART is DISABLED.";
        }

        if (featureFlag.isCallUserEnabled()) {
            log.info("Feature flag to call USER is ENABLED");
            productService.deleteUserProducts(id).subscribe(result -> log.info("Respuesta delete product from user: " + result.toString()));
        } else {
            log.info("Feature flag to call USER is DISABLED");
            message = message + " Feature flag to call USER is DISABLED.";
        }

        return ResponseHandler.generateResponse( message, HttpStatus.OK, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addProduct(@Valid @RequestBody ProductDTO product){
        Long id = productService.saveProduct(product);
        return ResponseHandler.generateResponse("DDBB updated",HttpStatus.CREATED,id);
    }

    @PostMapping("/JSON_load")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProductsFromJson(@RequestParam("path") String path) throws IOException {
            productService.updateProductsFromJson(path);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putProductById(@PathVariable Long id, @Valid @RequestBody ProductDTO newProduct) {
        productService.putProductById(newProduct, id);

        String message = "Product with id " + id + " updated successfully.";

        if (featureFlag.isCallCartEnabled()) {
            log.info("Feature flag to call CART is ENABLED");
            productService.patchCartProducts(newProduct, id).subscribe(result -> log.info("Update product from cart response: " + result.toString()));
        } else {
            log.info("Feature flag to call CART is DISABLED");
            message = message + " Feature flag to call CART is DISABLED.";
        }

        return ResponseHandler.generateResponse(message,HttpStatus.OK,id);
    }

    @PutMapping("/updateStock/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStock(@PathVariable Long id, @RequestBody Integer units) throws Exception {
        productService.updateStock(units, id);
    }
}