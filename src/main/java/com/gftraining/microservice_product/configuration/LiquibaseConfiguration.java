package com.gftraining.microservice_product.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class LiquibaseConfiguration {
    DataSourceConfiguration dataSource;

    public LiquibaseConfiguration(DataSourceConfiguration dataSource) {
        this.dataSource = dataSource;
    }
    @DependsOn("entityManagerFactory")
    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setDataSource(dataSource.dataSource());
        return liquibase;
    }

}
