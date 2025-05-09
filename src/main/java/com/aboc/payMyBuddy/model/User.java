package com.aboc.payMyBuddy.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", nullable = false, unique = true, length = 45)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 250)
    private String password;

    @Column(name = "solde")
    private BigDecimal solde;

    @Transient
    private String role;

    //Constructor
    public User() {
    }

    public User(String username, String email, String password, BigDecimal solde) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.solde = solde;
    }

    //Getter et Setter
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

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

