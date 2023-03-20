package com.cleanup.service.implementations;

import com.cleanup.config.CommandLineProperties;
import com.cleanup.model.User;
import com.cleanup.repository.TemplateRepository;
import com.cleanup.service.interfaces.MailService;
import com.cleanup.utility.Constants;
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

    public void sendWelcomeMessage(User recipient) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("adminEmail", properties.getEmail());
        send(context, recipient.getEmail(), "Welcome");
    }

    public void sendPasswordChangeToken(User recipient, long token) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("token", token);
        send(context, recipient.getEmail(), "PasswordChangeRequest");
    }

    public void sendPasswordChangeConfirmation(User recipient) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("adminEmail", properties.getEmail());
        send(context, recipient.getEmail(), "PasswordChangeResponse");
    }

    @Override
    public void resendPasswordChangeToken(User user, long generateToken) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(user));
        context.put("adminEmail", properties.getEmail());
        send(context, user.getEmail(), "PasswordChangeResend");
    }

    private String resolveRecipientName(User user) {
        if (user.isCustomUsername()) {
            return user.getUsername();
        } else if (user.getFirstName() != null) {
            return user.getFirstName();
        } else {
            return user.getEmail();
        }
    }

    private void send(Map<String, Object> context, String to, String emailTemplateName) {
        String template = templateRepository.findByName(emailTemplateName).getBody();
        Mustache mustache = mustacheFactory.compile(new StringReader(template), emailTemplateName);
        StringWriter writer = new StringWriter();
        try {
            mustache.execute(writer, context).flush();
        } catch (IOException e) {
            // TODO: 19-Mar-23 log here
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(properties.getEmail());
        message.setText(writer.toString());
        mailSender.send(message);
    }
}
