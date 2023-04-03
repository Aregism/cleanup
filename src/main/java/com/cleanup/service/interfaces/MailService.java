package com.cleanup.service.interfaces;

import com.cleanup.model.User;
import org.springframework.scheduling.annotation.Async;

public interface MailService {

    @Async
    void sendWelcomeMessage(User recipient);

    @Async
    void sendPasswordChangeToken(User recipient, long token);

    @Async
    void sendPasswordChangeConfirmation(User recipient);

    @Async
    void resendPasswordChangeToken(User user, long token);

    @Async
    void sendAccountVerification(User user, long token);

    @Async
    void resendAccountVerification(User user, long token);

    @Async
    void sendWarnAccountLock(User user);

    @Async
    void sendAccountLocked(User user);
}
