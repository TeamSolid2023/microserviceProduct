package com.gftraining.microservice_product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix="static-table")
public class Categories {
    private Map<String, Integer> categories;
}
