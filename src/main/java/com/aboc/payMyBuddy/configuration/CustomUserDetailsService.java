package com.aboc.payMyBuddy.configuration;


import com.aboc.payMyBuddy.model.User;
import com.aboc.payMyBuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User userDb = userRepository.findUserByUsername(username);

        return new org.springframework.security.core.userdetails.User(userDb.getUsername(), userDb.getPassword(), getGrantedAuthorities(userDb.getRole()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role){
        if (role == null) {
            role = "USER"; // ou une autre valeur par défaut
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE" + role));
        return authorities;
    }
}
