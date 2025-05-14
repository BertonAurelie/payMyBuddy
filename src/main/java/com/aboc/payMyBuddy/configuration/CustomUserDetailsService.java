package com.aboc.payMyBuddy.configuration;


import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
        System.out.println("loadUserByUsername called with: " + username);
        UserDb userDb = userRepository.findUserByUsername(username);
        if (userDb == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Assigner un rôle par défaut si le rôle est null
        if (userDb.getRole() == null) {
            userDb.setRole("USER");
        }

        List<GrantedAuthority> authorities = getGrantedAuthorities(userDb.getRole());

        System.out.println("Returning CustomUserDetails with ID: " + userDb.getId() + userDb.getRole());
        return new CustomUserDetails(userDb, authorities);
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role){

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + "USER"));
        return authorities;
    }
}
