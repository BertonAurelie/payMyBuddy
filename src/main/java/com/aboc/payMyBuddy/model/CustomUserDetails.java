package com.aboc.payMyBuddy.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private int id;

    public CustomUserDetails(UserDb userDb, Collection<? extends GrantedAuthority> authorities) {
        super(userDb.getUsername(), userDb.getPassword(), authorities);
        this.id = userDb.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
