package com.gftraining.microservice_product.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import com.gftraining.microservice_product.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    ProductEntity productEntity2 = new ProductEntity(1L,"Pelota", "Juguetes", "pelota futbol",new BigDecimal(19.99),20);

    @Test
    void testGetAll() throws Exception {
        when(productService.getAll())
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
        when(productService.getProductById(1L)).thenReturn(productEntity);

        mockmvc.perform(get("/products/{id}",1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productEntity)));
    }

    @Test
    void getProductByName() throws Exception {

        when(productService.getProductByName("Playmobil")).thenReturn(productListSameName);

        mockmvc.perform(get("/products/name/{name}","Playmobil"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productListSameName)));
    }

    @Test
    void updateProductsFromJson() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.post("/products/JSON_load")
                        .param("path", "C:\\Files\\data.json"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(productService,times(1)).updateProductsFromJson("C:\\Files\\data.json");
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

        mockmvc.perform(put("/products/{id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productEntity)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(productService.getProductById(1L), productEntity);
    }

    @Test
    void updateStock() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productEntity);
        doNothing().when(productService).updateStock(4, 1L);

        mockmvc.perform(patch("/products/updateStock/{id}",1L)
                .param("id", "1").contentType(MediaType.APPLICATION_JSON)
                        .content("5"))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(productService, Mockito.times(1)).updateStock(5, 1L);
    }
}
