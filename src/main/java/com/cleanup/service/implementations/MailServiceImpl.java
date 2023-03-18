package com.cleanup.service.implementations;

import com.cleanup.config.CommandLineProperties;
import com.cleanup.model.User;
import com.cleanup.repository.TemplateRepository;
import com.cleanup.service.interfaces.MailService;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    public MailServiceImpl(JavaMailSender mailSender, MustacheFactory mustacheFactory, TemplateRepository templateRepository, CommandLineProperties properties) {
        this.mailSender = mailSender;
        this.mustacheFactory = mustacheFactory;
        this.templateRepository = templateRepository;
        this.properties = properties;
    }

    private final JavaMailSender mailSender;
    private final MustacheFactory mustacheFactory;
    private final TemplateRepository templateRepository;
    private final CommandLineProperties properties;

    @Override
    public void sendWelcomeMessage(User recipient) {

        SimpleMailMessage message = new SimpleMailMessage();
        String template = templateRepository.findByName("Welcome").getBody();
        Mustache mustache = mustacheFactory.compile(new StringReader(template), "Welcome");
        StringWriter writer = new StringWriter();
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("adminEmail", properties.getEmail());
        try {
            mustache.execute(writer, context).flush();
        } catch (IOException ignored) {

        }
        String emailBody = writer.toString();

        message.setTo(recipient.getEmail());
        message.setFrom(properties.getEmail());
        message.setText(emailBody);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordChangeToken(String to, int token) {

    }

    private String resolveRecipientName(User user) {
        if (user.getUsername() != null) {
            return user.getUsername();
        } else if (user.getFirstName() != null) {
            return user.getFirstName();
        } else {
            return user.getEmail();
        }
    }
}
