package com.gftraining.microservice_product.end_to_end;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftraining.microservice_product.configuration.FeatureFlagsConfig;
import com.gftraining.microservice_product.model.ProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = BEFORE_TEST_METHOD)
public class ProductE2E {

    @Autowired
    private MockMvc mockmvc;

    @MockBean
    private FeatureFlagsConfig featureFlag;

    ProductDTO productDTO = new ProductDTO("Pelota", "Juguetes","pelota de futbol",new BigDecimal(19.99),24);

    @Test
    @DisplayName("Given the feature flags false, When perform all the sequence, Then the product has had to be removed")
    void deletePath() throws Exception {
        given(featureFlag.isCallUserEnabled()).willReturn(false);
        given(featureFlag.isCallUserEnabled()).willReturn(false);

        mockmvc.perform(post("/products")
                        .content(asJsonString(productDTO))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("{\"id\":14,\"message\":\"DDBB updated\",\"status\":201}"));

        mockmvc.perform(get("/products/id/{id}", 14))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{id: 14, name: Pelota, category: Juguetes, description:\"pelota de futbol\", price: 19.99, stock: 24, finalPrice: 15.99}"));

        mockmvc.perform(delete("/products/{id}", 14))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        mockmvc.perform(get("/products/id/{id}", 14))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
