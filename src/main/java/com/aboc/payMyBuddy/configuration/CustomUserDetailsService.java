package com.aboc.payMyBuddy.configuration;


import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security service to load user details from the database.
 * Implements UserDetailsService for authentication via email.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their email (used as username in Spring Security).
     *
     * @param email the email (username) of the user to load
     * @return a UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if the user does not exist
     * @throws RequestException          if the user is not found (custom exception thrown here)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("user research...{}", email);
        UserDb userDb = userRepository.findUserByEmail(email);

        if (userDb == null) {
            throw new RequestException("Unknown user");
        }

        //Assigner un rôle par défaut si le rôle est null
        if (userDb.getRole() == null) {
            userDb.setRole("USER");
        }

        List<GrantedAuthority> authorities = getGrantedAuthorities(userDb.getRole());

        logger.info("Returning CustomUserDetails with ID: {}{}", userDb.getId(), userDb.getRole());
        return new CustomUserDetails(userDb, authorities);
    }

    /**
     * Creates a list of Spring Security authorities from the user's role.
     *
     * @param role the user's role from the database (e.g. "USER", "ADMIN")
     * @return a list of GrantedAuthority with the "ROLE_" prefix
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + "USER"));

        return authorities;
    }
}
