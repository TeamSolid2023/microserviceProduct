package com.gftraining.microserviceProduct.services;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    ProductService service;
    @Mock
    ProductRepository repository;
    @Test
    void deleteProductById() {
       service.deleteProductById(1L);

       verify(repository,times(1)).deleteById(anyLong());
    }

    @Test
    void saveProduct() {
        ProductEntity product = new ProductEntity(109L,"A", new CategoryEntity(1L, "Libros", 20),"B", 2, 25);

        when(repository.save(product)).thenReturn(product);
        Long id = repository.save(product).getId();

        assertEquals(109, id);
    }

    @Test
    void getProductById() {
        ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
                new CategoryEntity(1L,"Juguetes",20),"pelota futbol",19.99,24);

        when(repository.findById(anyLong())).thenReturn(Optional.of(productEntity));
        assertThat(service.getProductById(1398L)).isEqualToComparingFieldByFieldRecursively(productEntity);
    }
}