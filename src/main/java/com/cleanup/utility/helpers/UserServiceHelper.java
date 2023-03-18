package com.cleanup.utility.helpers;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserServiceHelper {

    public long generateToken(){
        Random random = new Random();
        return random.nextLong();
    }
}
