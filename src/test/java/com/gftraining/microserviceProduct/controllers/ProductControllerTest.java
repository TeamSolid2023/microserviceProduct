package com.gftraining.microserviceProduct.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockmvc;
    @MockBean
    ProductService service;

    @Test
    void deleteProductById() throws Exception {

        mockmvc.perform(MockMvcRequestBuilders.delete("/products/deleteById/{id}",1l))
                                                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service,times(1)).deleteProductById(anyLong());
    }

    @Test
    void getProductById_Test() throws Exception {

        ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
                new CategoryEntity(2L,"Juguetes",20),"pelota futbol",19.99,24);

        Long id = 1398L;

        when(productService.getProductById(id)).thenReturn(productEntity);

        MvcResult result = mockMvc.perform(get("/products/{id}",id))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(productEntity.toString(), result.getResponse().getContentAsString());
    }
}