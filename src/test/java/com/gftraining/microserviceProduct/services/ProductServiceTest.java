package com.gftraining.microserviceProduct.services;


import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
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
            new CategoryEntity(1L,"Juguetes",20),"pelota futbol",19.99,24);

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
        ProductEntity product = new ProductEntity(109L,"A", new CategoryEntity(1L, "Libros", 20),"B", new BigDecimal(2), 25);

        when(repository.save(product)).thenReturn(product);
        Long id = repository.save(product).getId();

        assertEquals(109, id);
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
        service.updateProductsFromJson();

        verify(repository,times(1)).deleteAll();
        verify(repository,times(1)).saveAll(any());
    }
}