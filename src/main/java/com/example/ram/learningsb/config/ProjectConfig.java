package com.example.ram.learningsb.config;

import com.example.ram.learningsb.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan (basePackages = {"com.example.ram.learningsb.repositories", "com.example.ram.learningsb.controllers",
                                "com.example.ram.learningsb.services", "com.example.ram.learningsb.aspects"})
public class ProjectConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HashMap<String, String> props = new HashMap<>(Map.of("hibernate.show_sql","true"));
        return new HibernatePersistenceProvider().createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
    }
}
