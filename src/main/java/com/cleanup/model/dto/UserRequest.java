package com.cleanup.model.dto;

import com.cleanup.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRequest {

    private String firstName;

    private String middleName;

    private String lastName;

    private Gender gender = Gender.NOT_SPECIFIED;

    private String username;

    private String email;

    private String password;

    private LocalDate dateOfBirth;

    private boolean subscribe = true;

}
