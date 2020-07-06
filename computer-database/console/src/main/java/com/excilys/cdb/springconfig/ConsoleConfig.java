package com.excilys.cdb.springconfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.excilys.cdb.ui")
public class ConsoleConfig {

    private static AnnotationConfigApplicationContext context;

    public static AnnotationConfigApplicationContext getContext() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext(ConsoleConfig.class, ServiceConfig.class,
                    PersistenceConfig.class);
        }
        return context;
    }

}
