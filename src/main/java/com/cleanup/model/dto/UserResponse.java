package com.cleanup.model.dto;

import com.cleanup.model.Authority;
import com.cleanup.model.enums.AccountStatus;
import com.cleanup.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserResponse {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Gender gender;

    private String username;

    private String email;

    private LocalDate dateOfBirth;

    private boolean isSubscribe;

    private LocalDateTime registrationDate;

    private LocalDateTime verificationDate;

    private long token;

    private LocalDateTime tokenGeneratedDate;

    private LocalDateTime latestLogin;

    private byte failedLoginAttempts;

    private AccountStatus accountStatus;

    private boolean isAccountLocked;

    private boolean isBanned;

    private boolean isSubscribed;

    private Set<Authority> authorities;
}
