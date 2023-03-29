package com.gftraining.microserviceProduct.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import io.swagger.v3.core.util.Json;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockmvc;
    @MockBean
    ProductService productService;

    List<ProductEntity> productList = Arrays.asList(
            new ProductEntity(1L, "Playmobil", new CategoryEntity(1L, "Juguetes", 20), "juguetes de plástico", 40.00, 100)
            , new ProductEntity(2L, "Espaguetis", new CategoryEntity(4L, "Comida", 25), "pasta italiana elaborada con harina de grano duro y agua", 2.00, 220)
    );

    @Test
    void testGetAll() throws Exception {
        Mockito.when(productService.allProducts())
                .thenReturn(productList);

        mockmvc.perform(MockMvcRequestBuilders.get("/products/getAll"))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(productList)))
                .andReturn();
    }

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