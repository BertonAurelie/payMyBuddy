package com.aboc.payMyBuddy.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * DTO for transferring user information during HTTP requests
 * Used for creating a user mapping(POST Status)
 * does not use solde attribute for this DTO. Only username, email and password.
 */
public class CreatedUserDto {

    @NotBlank(message = "username may not be empty")
    private String username;

    @NotBlank(message = "email may not be empty")
    private String email;

    @NotBlank(message = "password may not be empty")
    private String password;

    //Constructor
    public CreatedUserDto(){}

    //GETTER SETTER

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
