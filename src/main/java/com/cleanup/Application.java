package com.cleanup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty("spring.datasource.username", args[0]);
        properties.setProperty("spring.datasource.password", args[1]);
        properties.setProperty("superadmin.password", args[2]);
        properties.setProperty("spring.mail.username", args[3]);
        properties.setProperty("spring.mail.password", args[4]);

        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
