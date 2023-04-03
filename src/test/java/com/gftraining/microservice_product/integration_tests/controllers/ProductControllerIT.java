package com.gftraining.microservice_product.integration_tests.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIT {
    @Autowired
    private MockMvc mockmvc;

    ProductDTO productEntity = new ProductDTO(new ProductEntity(4L, "Pelota",
            "Juguetes","pelota de futbol",new BigDecimal(19.99),24));


    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void testGetAll() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.get("/products/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Wonder", "Los Surcos del Azar", "Pelota")));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void getProductById() throws Exception {
        mockmvc.perform(get("/products/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{id: 1, name: Wonder, stock: 90}"));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void getProductByName() throws Exception {
        mockmvc.perform(get("/products/name/{name}","Los Surcos del Azar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[{id:2, name: \"Los Surcos del Azar\", price: 24.89}]"));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void putProductById() throws Exception {
        mockmvc.perform(put("/products/{id}",1).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productEntity)))
                .andExpect(status().isOk());

    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void deleteProductById() throws Exception {
        mockmvc.perform(delete("/products/{id}",1))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void updateProductsFromJson() throws Exception {
        //Put your own path
        mockmvc.perform(MockMvcRequestBuilders.post("/products/JSON_load")
                        .param("path", "C:\\Files\\data_test.json"))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void addNewProduct() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.post("/products/newProduct")
                        .content(asJsonString(productEntity))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("4"));
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
