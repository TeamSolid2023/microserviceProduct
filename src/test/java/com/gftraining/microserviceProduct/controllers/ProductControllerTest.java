package com.gftraining.microserviceProduct.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microserviceProduct.model.CategoryEntity;
import com.gftraining.microserviceProduct.model.ProductDTO;
import com.gftraining.microserviceProduct.model.ProductEntity;
import com.gftraining.microserviceProduct.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.webjars.NotFoundException;

import java.util.Arrays;
import java.util.List;


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

    ProductEntity productEntity = new ProductEntity(1398L,"Pelota",
            new CategoryEntity(1L,"Juguetes",20),"pelota futbol",19.99,24);

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
    void addNewProduct() throws Exception {
        ProductEntity product = new ProductEntity(109L,"A", new CategoryEntity(1L, "Libros", 20),"B", 2, 25);

        Mockito.when(productService.saveProduct(Mockito.any(ProductDTO.class))).thenReturn(product.getId());

        mockmvc.perform(MockMvcRequestBuilders.post("/products/newProduct")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getProductById() throws Exception {

        when(productService.getProductById(1398L)).thenReturn(productEntity);

        mockmvc.perform(get("/products/{id}",1398L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("{\"id\":1398,\"name\":\"Pelota\",\"category\":{\"id\":1,\"name\":\"Juguetes\",\"discount\":20}" +
                        ",\"description\":\"pelota futbol\",\"price\":19.99,\"stock\":24}"));
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

        mockmvc.perform(get("/products/getByName/{name}","Pelota"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("{\"id\":1398,\"name\":\"Pelota\",\"category\":{\"id\":1,\"name\":\"Juguetes\",\"discount\":20}" +
                        ",\"description\":\"pelota futbol\",\"price\":19.99,\"stock\":24}"));
    }

    @Test
    void getProductByName_ThrowsNotFound() throws Exception {

        when(productService.getProductByName(anyString())).thenReturn(null);

        mockmvc.perform(get("/products/getByName/{name}","Pera"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}