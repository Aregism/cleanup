package com.cleanup.model;

import com.cleanup.model.enums.AccountStatus;
import com.cleanup.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "MiddleName")
    private String middleName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Gender")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.NOT_SPECIFIED;

    @Column(unique = true, nullable = false, name = "Username")
    private String username;

    @Column(unique = true, nullable = false, name = "Email")
    @NotNull
    private String email;

    @Column(nullable = false, name = "Password")
    @NotNull
    private String password;

    @Column(name = "DateOfBirth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, name = "RegistrationDate")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "VerificationDate")
    private LocalDateTime verificationDate;

    @Column(name = "Token")
    private Long token;

    @Column
    private LocalDateTime tokenGeneratedDate;

    @Column(name = "LatestLogin")
    private LocalDateTime latestLogin;

    @Column(name = "PasswordToBeSet")
    private String passwordToBeSet;

    @Column(name = "PasswordChangeDate")
    private LocalDateTime passwordChangeDate;

    @Column(name = "FailedLoginAttempts")
    private byte failedLoginAttempts;

    @Column(name = "AccountStatus")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.UNVERIFIED;

    @Column(name = "IsAccountLocked")
    private boolean isAccountLocked;

    @Column(name = "IsBanned")
    private boolean isBanned;

    @Column(name = "IsSubscribed")
    private boolean isSubscribed;

    @Column(name = "CustomUsername")
    private boolean customUsername;

    @ManyToMany
    @JsonManagedReference("users")
    private Set<Authority> authorities;

}
