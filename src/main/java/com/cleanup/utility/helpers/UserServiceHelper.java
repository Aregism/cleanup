package com.cleanup.utility.helpers;

import com.cleanup.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class UserServiceHelper {

    public long generateToken(){
        Random random = new Random();
        return Math.abs(random.nextLong());
    }

    public void setupPasswordChangeUser(User user) {
        user.setToken(null);
        user.setPassword(user.getPasswordToBeSet());
        user.setPasswordToBeSet(null);
        user.setPasswordChangeDate(LocalDateTime.now());
        user.setFailedLoginAttempts((byte) 0);
        user.setAccountLocked(false);
    }
}
