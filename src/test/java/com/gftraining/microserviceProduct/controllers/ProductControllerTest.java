package com.gftraining.microserviceProduct.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockmvc;
    @MockBean
    ProductService productService;

    @Test
    void deleteProductById() throws Exception {

        mockmvc.perform(MockMvcRequestBuilders.delete("/products/deleteById/{id}",1l))
                                                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(productService,times(1)).deleteProductById(anyLong());
    }

    @Test
    void getProductById_Test() throws Exception {

        ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
                new CategoryEntity(1L,"Juguetes",20),"pelota futbol",19.99,24);

        when(productService.getProductById(anyLong())).thenReturn(productEntity);

        mockmvc.perform(get("/products/{id}",1398L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("{\"id\":1398,\"name\":\"Pelota\",\"category\":{\"id\":1,\"name\":\"Juguetes\",\"discount\":20}" +
                        ",\"description\":\"pelota futbol\",\"price\":19.99,\"stock\":24}"));
    }
}