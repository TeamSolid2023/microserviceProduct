package com.gftraining.microservice_product.controllers;

import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.services.ProductService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@Log4j2
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
    public List<ProductEntity> getProductByName(@PathVariable String name) {
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
    public void updateProductsFromJson(@RequestParam("path") String path) throws IOException {
            productService.updateProductsFromJson(path);
    }

    @PutMapping("/{id}")
    public void putProductById(@PathVariable Long id, @RequestBody ProductDTO newProduct) {
        productService.putProductById(newProduct, id);
    }
}
