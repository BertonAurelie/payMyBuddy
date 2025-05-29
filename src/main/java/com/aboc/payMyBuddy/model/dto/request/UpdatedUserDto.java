package com.aboc.payMyBuddy.model.dto.request;

import jakarta.validation.constraints.Email;

public class UpdatedUserDto {
    private int id;
    private String username;
    @Email(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
    private String email;
    private String password;
    private double solde;

    public UpdatedUserDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }
}
