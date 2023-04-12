package com.gftraining.microservice_product.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.model.ProductDTO;
import com.gftraining.microservice_product.model.ProductEntity;
import org.junit.jupiter.api.DisplayName;
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

    ProductDTO productDTO = new ProductDTO("Pelota", "Juguetes","pelota de futbol",new BigDecimal(19.99),24);

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("When perform get request /products/getAll, Then is expected to have status of 200, be an ArrayList, be a Json and have size 13")
    void testGetAll() throws Exception {
        mockmvc.perform(get("/products/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.*", hasSize(13)));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given an id, When perform get request /products/id/{id}, Then is expected to have status of 200, be a Json and have {id: 1, name: Wonder, stock: 90}")
    void getProductById() throws Exception {
        mockmvc.perform(get("/products/id/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{id: 1, name: Wonder, stock: 90}"));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given a name, When perform get request /products/name/{name}, Then is expected to have status of 200, be a Json and have size 2")
    void getProductByName() throws Exception {
        mockmvc.perform(get("/products/name/{name}","Los Surcos del Azar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.*", hasSize(2)));
    
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given an id, When perform put request /products/{id}, Then is expected to have status of 201")
    void putProductById() throws Exception {
        mockmvc.perform(put("/products/{id}",1).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given an id, When perform delete request /products/{id}, Then is expected to have status of 204")
    void deleteProductById() throws Exception {
        mockmvc.perform(delete("/products/{id}",7))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given a path, When perform post request /products/JSON_load, Then is expected to have status of 201")
    void updateProductsFromJson() throws Exception {
        //Put your own path
        mockmvc.perform(post("/products/JSON_load")
                        .param("path", "C:\\Files\\data.json"))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    @DisplayName("Given a Product, When perform post request /products, Then is expected to have status of 201, be a Json and have {\"id\":14,\"message\":\"DDBB updated\",\"status\":201}")
    void addNewProduct() throws Exception {
        mockmvc.perform(post("/products")
                        .content(asJsonString(productDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("{\"id\":14,\"message\":\"DDBB updated\",\"status\":201}"));
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
