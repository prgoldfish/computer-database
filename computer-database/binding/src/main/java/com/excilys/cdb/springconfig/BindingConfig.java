package com.excilys.cdb.springconfig;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = "com.excilys.cdb.validation")
public class BindingConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource res = new ReloadableResourceBundleMessageSource();
        res.setBasenames("WEB-INF/errorMessages", "i18n/messages");
        //res.setDefaultEncoding("UTF-8");
        return res;
    }

}
