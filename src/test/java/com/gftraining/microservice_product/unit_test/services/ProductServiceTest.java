package com.gftraining.microservice_product.unit_test.services;


import com.gftraining.microservice_product.configuration.Categories;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.repositories.ProductRepository;
import com.gftraining.microservice_product.services.CartWebClient;
import com.gftraining.microservice_product.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import org.mockito.BDDMockito;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService service;
    @Mock
    ProductRepository repository;
    @Mock
    Categories categories;
    @Mock
    CartWebClient cartWebClient;

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
        given(repository.findAll()).willReturn(productList);
        assertThat(service.getAll()).isEqualTo(productList);
    }

    @Test
    @DisplayName("given a product id, when delete product by id, then the product is deleted")
    void deleteProductById() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.of(productEntity));
        
        //when
       service.deleteProductById(1L);
       
       //then
       verify(repository).findById(anyLong());
       verify(repository).deleteById(anyLong());
    }

    @Test
    void saveProduct() {
        given(repository.save(productEntity)).willReturn(productEntity);
        Long id = repository.save(productEntity).getId();
        verify(repository).save(any());
        assertEquals(1L, id);
    }

    @Test
    void getProductById() {
        given(repository.findById(1L)).willReturn(Optional.of(productEntity));

        assertThat(service.getProductById(1L)).isEqualToComparingFieldByFieldRecursively(productEntity);
    }

    @Test
    void getProductByName() {
        given(repository.findAllByName("Playmobil")).willReturn(productListSameName);

        assertThat(service.getProductByName("Playmobil")).isEqualTo(productListSameName);
    }

    @Test
    void updateDatabase() throws IOException {
        //Put your own path
        service.updateProductsFromJson("C:\\Files\\data.json");

        verify(repository).deleteAll();
        verify(repository).saveAll(any());
    }

    @Test
    void putProductById() {
        Map<String, Integer> cat = new HashMap<String, Integer>();
        cat.put("Juguetes", 20);

        given(repository.findById(1L)).willReturn(Optional.of(productEntity));
        given(categories.getCategories()).willReturn(cat);

        service.putProductById(productDTO, 1L);

        verify(repository).findById(anyLong());
        verify(repository).save(any());
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
