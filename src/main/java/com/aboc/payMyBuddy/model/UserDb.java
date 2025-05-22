package com.aboc.payMyBuddy.model;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class UserDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, length = 45)
    private String username;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "password", length = 250)
    private String password;

    @Column(name = "solde")
    private BigDecimal solde;

    @Transient
    private String role;

    @ManyToMany(
        fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE
            }
    )
    @JoinTable(
        name = "user_relation", //nom de la table de jointure
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<UserDb> friends = new HashSet<>();

    //Constructor
    public UserDb() {
    }

    public UserDb(String username, String email, String password, BigDecimal solde) {
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

    public Set<UserDb> getFriends() {
        return friends;
    }

    public void setFriends(Set<UserDb> friends) {
        this.friends = friends;
    }
}

