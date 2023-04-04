package com.gftraining.microservice_product.unit_test.services;


import com.gftraining.microservice_product.configuration.Categories;
import com.gftraining.microservice_product.exception.GlobalExceptionHandler;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService service;
    @Mock
    ProductRepository repository;
    @Mock
    Categories yaml;

    List<ProductEntity> productList = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Espaguetis", "Comida", "pasta italiana elaborada con harina de grano duro y agua", new BigDecimal(20.00), 220)
    );
    List<ProductEntity> productListSameName = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100)
    );
    ProductEntity productEntity = new ProductEntity(1L,"Pelota", "Juguetes","pelota futbol",new BigDecimal(19.99),24);

    ProductDTO productDTO = new ProductDTO(productEntity.getName(), productEntity.getCategory(), productEntity.getDescription(), productEntity.getPrice(), productEntity.getStock());

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(productList);
        assertThat(service.getAll()).isEqualTo(productList);
    }

    @Test
    void deleteProductById() {
       service.deleteProductById(1L);
       verify(repository,times(1)).deleteById(anyLong());
    }

    @Test
    void saveProduct() {
        when(repository.save(productEntity)).thenReturn(productEntity).thenThrow();
        Long id = repository.save(productEntity).getId();

        assertEquals(1L, id);
    }

    @Test
    void getProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(productEntity));

        assertThat(service.getProductById(1L)).isEqualToComparingFieldByFieldRecursively(productEntity);
    }

    @Test
    void getProductByName() {
        when(repository.findAllByName("Playmobil")).thenReturn(productListSameName);

        assertThat(service.getProductByName("Playmobil")).isEqualTo(productListSameName);
    }

    @Test
    void updateDatabase() throws IOException {
        //Put your own path
        service.updateProductsFromJson("C:\\Files\\data_test.json");

        verify(repository,times(1)).deleteAll();
        verify(repository,times(1)).saveAll(any());
    }

    @Test
    void putProductById() {
        Map<String, Integer> cat = new HashMap<String, Integer>();
        cat.put("Juguetes", 20);

        when(repository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(yaml.getCategory()).thenReturn(cat);

        service.putProductById(productDTO, 1L);

        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).save(any());
    }

    @Test
    void calculateFinalPrice(){
        BigDecimal realParam = new BigDecimal("23.85");
        BigDecimal expectedParam = new BigDecimal("21.47");

        assertThat(service.calculateFinalPrice(realParam, 10)).isEqualByComparingTo(expectedParam);
    }

    @Test
    void getDiscount(){
        assertThat(service.getDiscount(productEntity)).isZero();
    }
}
