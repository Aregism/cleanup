package com.cleanup.service.implementations;

import com.cleanup.config.CommandLineProperties;
import com.cleanup.model.User;
import com.cleanup.repository.TemplateRepository;
import com.cleanup.service.interfaces.MailService;
import com.cleanup.utility.Constants;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Log4j2
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
        send(context, recipient.getEmail(), "Welcome", "Welcome");
    }

    public void sendPasswordChangeToken(User recipient, long token) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("link", Constants.BASE_URL + "/users/pw-confirm/" + token);
        send(context, recipient.getEmail(), "Password change link", "PasswordChangeRequest");
    }

    public void sendPasswordChangeConfirmation(User recipient) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(recipient));
        context.put("adminEmail", properties.getEmail());
        send(context, recipient.getEmail(), "Confirm password change", "PasswordChangeResponse");
    }

    public void resendPasswordChangeToken(User user, long token) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(user));
        context.put("link", Constants.BASE_URL + "/users/pw-confirm/" + token);
        send(context, user.getEmail(), "Your new link", "PasswordChangeResend");
    }

    public void sendAccountVerification(User user, long token) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(user));
        context.put("link", Constants.BASE_URL + "/users/verify/" + token);
        send(context, user.getEmail(), "Account verification", "AccountVerification");
    }

    public void resendAccountVerification(User user, long token) {

    }

    public void sendWarnAccountLock(User user) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(user));
        context.put("link", Constants.BASE_URL + "/users/pw-change-request");
        send(context, user.getEmail(), "Account lock warning", "WarnAccountLock");
    }

    @Override
    public void sendAccountLocked(User user) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", resolveRecipientName(user));
        context.put("link", Constants.BASE_URL + "/users/pw-change-request");
        send(context, user.getEmail(), "Account locked", "AccountLocked");
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

    private void send(Map<String, Object> context, String to, String subject, String emailTemplateName) {
        String template = templateRepository.findByName(emailTemplateName).getBody();
        Mustache mustache = mustacheFactory.compile(new StringReader(template), emailTemplateName);
        StringWriter writer = new StringWriter();
        try {
            mustache.execute(writer, context).flush();
        } catch (IOException e) {
            log.error("Could not create mustache template.");
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        try {
            helper.setTo(to);
            helper.setFrom(properties.getEmail());
            helper.setSubject(subject);
            helper.setText(writer.toString(), true);
            mailSender.send(message);
            log.info("Email successfully sent. Subject: " + message.getSubject());
        } catch (MessagingException e) {
            log.fatal("Could not send email.");
        }

    }
}
