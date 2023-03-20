package com.cleanup.utility;

import com.cleanup.config.CommandLineProperties;
import com.cleanup.model.Authority;
import com.cleanup.model.User;
import com.cleanup.model.enums.AccountStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

public class Constants {

    // Temporal units
    public static final int ONE_HOUR = 1;
    public static final int TWO_HOURS = 2;
    public static final int THREE_HOURS = 3;
    public static final int SIX_HOURS = 6;
    public static final int TWELVE_HOURS = 12;
    public static final int ONE_DAY = 24;
    public static final int ONE_WEEK = 168;

    // Roles
    public static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // Authorities
    public static final Authority AUTHORITY_SUPERADMIN = new Authority(ROLE_SUPERADMIN);
    public static final Authority AUTHORITY_ADMIN = new Authority(ROLE_ADMIN);
    public static final Authority AUTHORITY_USER = new Authority(ROLE_USER);
    public static final Set<Authority> ALL_AUTHORITIES = Set.of(AUTHORITY_SUPERADMIN, AUTHORITY_ADMIN, AUTHORITY_USER);

    // Admin Users
    public static User USER_SUPERADMIN = new User();
    public static Set<User> ALL_ADMINS = new HashSet<>();

    // Regexes
    public static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";

    // Logger constants
    public static final String LOG_DIRECTORY = "target/logs";

    public static void setupAdmins(CommandLineProperties properties, PasswordEncoder passwordEncoder) {
        USER_SUPERADMIN.setUsername("superadmin");
        USER_SUPERADMIN.setEmail(properties.getEmail());
        USER_SUPERADMIN.setPassword(passwordEncoder.encode(properties.getSuperadminPassword()));
        USER_SUPERADMIN.setAccountStatus(AccountStatus.VERIFIED);
        USER_SUPERADMIN.setAuthorities(ALL_AUTHORITIES);
        ALL_ADMINS.add(USER_SUPERADMIN);
    }
}
