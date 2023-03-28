package com.gftraining.microserviceProduct.controllers;

import com.gftraining.microserviceProduct.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

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
}