package com.gftraining.microserviceProduct.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
            "Juguetes","pelota futbol",new BigDecimal(19.99),24);

    @Test
    void testGetAll() throws Exception {
        when(productService.allProducts())
                .thenReturn(productList);

        mockmvc.perform(MockMvcRequestBuilders.get("/products/getAll"))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(productList)))
                .andReturn();
    }

    @Test
    void deleteProductById() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.delete("/products/{id}",1l))
                                                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(productService,times(1)).deleteProductById(anyLong());
    }

    @Test
    void addNewProduct() throws Exception {
        when(productService.saveProduct(any(ProductDTO.class))).thenReturn(productEntity.getId());

        mockmvc.perform(MockMvcRequestBuilders.post("/products/newProduct")
                        .content(asJsonString(productEntity))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getProductById() throws Exception {
        when(productService.getProductById(1398L)).thenReturn(productEntity);

        mockmvc.perform(get("/products/{id}",1398L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productEntity)));
    }

    @Test
    void getProductById_ThrowsNotFound() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(null);

        mockmvc.perform(get("/products/{id}",2L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getProductByName() throws Exception {
        when(productService.getProductByName("Pelota")).thenReturn(productEntity);

        mockmvc.perform(get("/products/name/{name}","Pelota"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productEntity)));
    }

    @Test
    void updateProductsFromJson() throws Exception {

        mockmvc.perform(MockMvcRequestBuilders.post("/products/JSON_load"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(productService,times(1)).updateProductsFromJson();
    }


  @Test
  void getProductByName_ThrowsNotFound() throws Exception {
        when(productService.getProductByName(anyString())).thenReturn(null);

        mockmvc.perform(get("/products/name/{name}","Pera"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void putProductById() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productEntity);

        mockmvc.perform(put("/products/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productEntity)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(productService.getProductById(1L), productEntity);
    }
}