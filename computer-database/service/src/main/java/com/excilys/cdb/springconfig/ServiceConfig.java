package com.excilys.cdb.springconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.excilys.cdb.service")
public class ServiceConfig {

}
