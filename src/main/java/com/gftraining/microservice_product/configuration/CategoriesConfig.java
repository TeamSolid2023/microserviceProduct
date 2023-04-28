package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "static-table")
public class CategoriesConfig {
    private Map<String, Integer> categories;
}
