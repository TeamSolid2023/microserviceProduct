package com.gftraining.microservice_product.unit_test.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.controllers.ProductController;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testGetAll() throws Exception {
        given(productService.getAll())
                .willReturn(productList);

        mockmvc.perform(MockMvcRequestBuilders.get("/products/getAll"))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(productList)))
                .andReturn();
    }

    @Test
    void deleteProductById() throws Exception {

        mockmvc.perform(MockMvcRequestBuilders.delete("/products/{id}",1l))
                                                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(productService).deleteProductById(anyLong());
    }

    @Test
    void addNewProduct() throws Exception {
        given(productService.saveProduct(any(ProductDTO.class))).willReturn(productEntity.getId());

        mockmvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(asJsonString(productEntity))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getProductById() throws Exception {
        given(productService.getProductById(1L)).willReturn(productEntity);

        mockmvc.perform(get("/products/id/{id}",1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productEntity)));
    }

    @Test
    void getProductByName() throws Exception {

        given(productService.getProductByName("Playmobil")).willReturn(productListSameName);

        mockmvc.perform(get("/products/name/{name}","Playmobil"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(asJsonString(productListSameName)));
    }

    @Test
    void updateProductsFromJson() throws Exception {
        //Put your own path
        mockmvc.perform(MockMvcRequestBuilders.post("/products/JSON_load")
                        .param("path", "C:\\Files\\data_test.json"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(productService).updateProductsFromJson("C:\\Files\\data_test.json");
    }

    @Test
    void putProductById() throws Exception {
        given(productService.getProductById(anyLong())).willReturn(productEntity);

        mockmvc.perform(put("/products/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productEntity)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        assertThat(productService.getProductById(1L)).isEqualTo(productEntity);
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
