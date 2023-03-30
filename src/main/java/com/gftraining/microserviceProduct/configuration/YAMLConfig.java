package com.gftraining.microserviceProduct.configuration;

import com.gftraining.microserviceProduct.model.CategoryEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("variables")
public class YAMLConfig {
    private List<CategoryEntity> category;

    public List<CategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryEntity> category) {
        this.category = category;
    }
}
