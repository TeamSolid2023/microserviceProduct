package com.gftraining.microserviceProduct.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix="my")
public class Categories {
    private Map<String, Integer> category;
}
