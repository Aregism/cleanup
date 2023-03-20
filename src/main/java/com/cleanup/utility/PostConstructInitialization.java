package com.cleanup.utility;

import com.cleanup.config.CommandLineProperties;
import com.cleanup.model.MailTemplate;
import com.cleanup.repository.AuthorityRepository;
import com.cleanup.repository.TemplateRepository;
import com.cleanup.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.cleanup.utility.Constants.*;

@Component
public class PostConstructInitialization {
    public PostConstructInitialization(
            AuthorityRepository authorityRepository,
            UserRepository userRepository,
            TemplateRepository templateRepository,
            CommandLineProperties properties,
            PasswordEncoder passwordEncoder) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
        this.templateRepository = templateRepository;
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final CommandLineProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final TemplateRepository templateRepository;

    @PostConstruct
    private void init() throws IOException {
            authorityRepository.saveAll(ALL_AUTHORITIES);
            setupAdmins(properties, passwordEncoder);
            userRepository.saveAll(ALL_ADMINS);

        File folder = new File("src/main/resources/templates");
        File[] files = folder.listFiles();
        List<MailTemplate> templates = new ArrayList<>();
        MailTemplate template;
        for (File file : files) {
            template = new MailTemplate();
            template.setName(file.getName().replace(".mustache", ""));
            template.setBody(new String(Files.readAllBytes(file.toPath())));
            templates.add(template);
        }
        templateRepository.saveAll(templates);
    }
}