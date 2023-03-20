package com.cleanup.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    @NotNull
    private String email;

    @NotNull
    private String newPassword1;

    @NotNull
    private String newPassword2;

}
