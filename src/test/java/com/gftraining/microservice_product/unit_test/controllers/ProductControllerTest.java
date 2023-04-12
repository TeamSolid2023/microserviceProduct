package com.gftraining.microservice_product.unit_test.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.controllers.ProductController;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.services.ProductService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockmvc;
    @MockBean
    ProductService productService;

    List<ProductEntity> productList = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100)
            , new ProductEntity(2L, "Espaguetis", "Comida", "pasta italiana elaborada con harina de grano duro y agua", new BigDecimal(2.00), 220)
    );
    List<ProductEntity> productListSameName = Arrays.asList(
            new ProductEntity(1L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100),
            new ProductEntity(2L, "Playmobil", "Juguetes", "juguetes de plástico", new BigDecimal(40.00), 100)
    );

    ProductEntity productEntity = new ProductEntity(1L,"Pelota", "Juguetes", "pelota futbol",new BigDecimal(19.99),24);

    @Test
    @DisplayName("Given a call in ProductService getAll, When perform the get request /products/getAll, Then return a list of Products")
    void testGetAll() throws Exception {
        given(productService.getAll()).willReturn(productList);

        mockmvc.perform(get("/products/getAll"))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(productList)))
                .andReturn();
    }

    @Test
    @DisplayName("Given an id, When perform the delete request /products/{id}, Then verify if the service is called and has no content")
    void deleteProductById() throws Exception {
        mockmvc.perform(delete("/products/{id}",1l))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductById(anyLong());
    }

    @Test
    @DisplayName("Given a Product, When calling service to add a new Product, Then the Product is created and is a Json")
    void addNewProduct() throws Exception {
        given(productService.saveProduct(any(ProductDTO.class))).willReturn(productEntity.getId());

        mockmvc.perform(post("/products")
                        .content(asJsonString(productEntity))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("given a product id, when calling service to find products by id, then the product is returned")
    void getProductById() throws Exception {
        given(productService.getProductById(1L)).willReturn(productEntity);

        mockmvc.perform(get("/products/id/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(productEntity)));
    }

    @Test
    @DisplayName("Given a product name, When calling service to find products by name, Then a list of products with that name is returned")
    void getProductByName() throws Exception {
        given(productService.getProductByName("Playmobil")).willReturn(productListSameName);

        mockmvc.perform(get("/products/name/{name}","Playmobil"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(productListSameName)));
    }

    @Test
    @DisplayName("Given a path, When perform the post request /products/JSON_load, Then verify if the service is called and has been created")
    void updateProductsFromJson() throws Exception {
        //Put your own path
        mockmvc.perform(post("/products/JSON_load")
                        .param("path", "C:\\Files\\data_test.json"))
                .andExpect(status().isCreated());

        verify(productService).updateProductsFromJson("C:\\Files\\data_test.json");
    }

    @Test
    @DisplayName("Given any long, When perform the put request /products/{id}, Then expect is created and is equal to productEntity")
    void putProductById() throws Exception {
        given(productService.getProductById(anyLong())).willReturn(productEntity);

        mockmvc.perform(put("/products/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productEntity)))
                .andExpect(status().isCreated());

        assertThat(productService.getProductById(1L)).isEqualTo(productEntity);
    }

    @Test
    @DisplayName("When calling the service with any Long, Then the product is returned")
    void updateStock() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productEntity);
        doNothing().when(productService).updateStock(4, 1L);

        mockmvc.perform(patch("/products/updateStock/{id}",1L)
                .param("id", "1").contentType(MediaType.APPLICATION_JSON)
                        .content("5"))
                        .andExpect(status().isOk());

        verify(productService).updateStock(5, 1L);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
