package com.cleanup.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "commandline")
@Getter
@Setter
public class CommandLineProperties {

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${superadmin.password}")
    private String superadminPassword;

    @Value("${spring.mail.username}")
    private String email;

    @Value("${spring.mail.password}")
    private String emailPassword;
}
