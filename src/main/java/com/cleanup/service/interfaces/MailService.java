package com.cleanup.service.interfaces;

import com.cleanup.model.User;
import org.springframework.scheduling.annotation.Async;

public interface MailService {

    @Async
    void sendWelcomeMessage(User recipient);

    @Async
    void sendPasswordChangeToken(String to, int token);
}
