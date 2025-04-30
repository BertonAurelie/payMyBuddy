package com.aboc.payMyBuddy.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreatedUserDto {

    private int id;

    @NotBlank(message = "firstName may not be empty")
    private String username;

    @NotBlank(message = "firstName may not be empty")
    private String email;

    @NotBlank(message = "firstName may not be empty")
    private String password;

    @NotBlank(message = "firstName may not be empty")
    private BigDecimal solde;
}
