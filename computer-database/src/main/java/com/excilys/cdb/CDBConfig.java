package com.excilys.cdb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = "com.excilys.cdb")
public class CDBConfig {

    private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CDBConfig.class);

    @Bean
    public HikariDataSource hikariDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public HikariConfig hikariConfig() {
        return new HikariConfig("/hikari.properties");
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }

}
