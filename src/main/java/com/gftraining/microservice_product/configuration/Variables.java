package com.gftraining.microservice_product.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;



@Configuration
@EnableConfigurationProperties
@Component
@ConfigurationProperties(prefix = "variables")
@Data
@NoArgsConstructor
public class Variables {
    private List<Category> category;
    @Data
    @NoArgsConstructor
    public static class Category{
        private int positionInList;
        private String name;
        private int discount;
    }

}
