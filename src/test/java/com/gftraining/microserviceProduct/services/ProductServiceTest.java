package com.gftraining.microserviceProduct.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.math.BigDecimal;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService service;
    @Mock
    ProductRepository repository;

    List<ProductEntity> productList = Arrays.asList(
            new ProductEntity(1L, "Playmobil", new CategoryEntity(1L, "Juguetes", 20), "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Espaguetis", new CategoryEntity(4L, "Comida", 25), "pasta italiana elaborada con harina de grano duro y agua", new BigDecimal(20.00), 220)
    );
    ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
            new CategoryEntity(1L,"Juguetes",20),"pelota futbol",new BigDecimal(19.99),24);

    @Test
    void testGetAll() {
        when(repository.findAll()).thenReturn(productList);
        assertThat(service.allProducts()).isEqualTo(productList);
    }

    @Test
    void deleteProductById() {
       service.deleteProductById(1L);
       verify(repository,times(1)).deleteById(anyLong());
    }

    @Test
    void saveProduct() {
        when(repository.save(productEntity)).thenReturn(productEntity);
        Long id = repository.save(productEntity).getId();

        assertEquals(1398L, id);
    }

    @Test
    void getProductById() {
        when(repository.findById(1398L)).thenReturn(Optional.of(productEntity));

        assertThat(service.getProductById(1398L)).isEqualToComparingFieldByFieldRecursively(productEntity);
    }

    @Test
    void getProductByName() {
        when(repository.findByName("Pelota")).thenReturn(productEntity);
        assertThat(service.getProductByName("Pelota")).isEqualToComparingFieldByFieldRecursively(productEntity);
    }

    @Test
    void updateDatabase() throws IOException {
        service.updateProductsFromJson("C:\\Files\\data.json");

        verify(repository,times(1)).deleteAll();
        verify(repository,times(1)).saveAll(any());
    }

    @Test
    void putProductById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(productEntity));
        service.putProductById(productEntity, 1L);
        verify(repository,times(1)).findById(anyLong());
        verify(repository,times(1)).save(any());
    }

    @Test
    void calculateFinalPrice(){
        BigDecimal realParam = new BigDecimal("23.85");
        BigDecimal expectedParam = new BigDecimal("21.47");

        assertThat(service.calculateFinalPrice(realParam, 10)).isEqualByComparingTo(expectedParam);
    }
}