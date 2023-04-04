package com.gftraining.microservice_product.configuration;


import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConditionalOnClass(value={liquibase.integration.spring.SpringLiquibase.class,liquibase.change.DatabaseChange.class})
@ConditionalOnBean(value=javax.sql.DataSource.class)
@ConditionalOnProperty(prefix="spring.liquibase",
        name="enabled",
        matchIfMissing=true)
@AutoConfigureAfter(value={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class LiquibaseAutoConfiguration{


}
