package com.gftraining.microservice_product.integration_tests;


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

    ProductDTO productDTO = new ProductDTO("Pelota", "Juguetes","pelota de futbol",new BigDecimal(19.99),24);

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void testGetAll() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.get("/products/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.*", hasSize(27)));
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void getProductById() throws Exception {
        mockmvc.perform(get("/products/id/{id}",1))
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
                .andExpect(jsonPath("$.*", hasSize(9)));
    
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void putProductById() throws Exception {
        mockmvc.perform(put("/products/{id}",1).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void deleteProductById() throws Exception {
        mockmvc.perform(delete("/products/{id}",18))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void updateProductsFromJson() throws Exception {
        //Put your own path
        mockmvc.perform(MockMvcRequestBuilders.post("/products/JSON_load")
                        .param("path", "C:\\Files\\data.json"))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
    void addNewProduct() throws Exception {
        mockmvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(asJsonString(productDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("Added new product with id: 28"));
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
