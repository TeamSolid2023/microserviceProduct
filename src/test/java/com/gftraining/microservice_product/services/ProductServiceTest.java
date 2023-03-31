package com.gftraining.microservice_product.services;


import com.gftraining.microservice_product.configuration.Categories;

import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;

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
    ProductEntity productEntity = new ProductEntity(1398L,"Pelota", "Juguetes","pelota futbol",new BigDecimal(19.99),24);

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

        when(repository.findAllByName("Playmobil")).thenReturn(productListSameName);
        assertThat(service.getProductByName("Playmobil")).isEqualTo(productListSameName);
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
        service.putProductById(new ProductDTO(productEntity), 1L);
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
        assertThat(service.getDiscount(productEntity)).isEqualTo(0);
    }
}
