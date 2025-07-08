package com.aboc.payMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 * <p>
 * Defines security rules such as:
 * Public access to static resources and login/registration pages.
 * Restricts all other endpoints to authenticated users with role USER.
 * Configures custom login page and login processing URL.
 * Sets password encoder to BCrypt with strength 12.
 * Wires a custom UserDetailsService for authentication.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configures the HTTP security including URL authorization and login settings.
     *
     * @param http the HttpSecurity to modify
     * @return the SecurityFilterChain that defines security filters and rules
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/css/**", "/images/**", "/login", "/registration").permitAll();
                    auth.anyRequest().hasRole("USER");
                })
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login").permitAll()
                        .usernameParameter("email")
                        .defaultSuccessUrl("/relationPage", true)
                )
                .build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean to hash passwords securely.
     * Uses a strength of 12, balancing security and performance.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures the AuthenticationManager with the custom UserDetailsService and password encoder.
     *
     * @param http                  the HttpSecurity context
     * @param bCryptPasswordEncoder the password encoder bean
     * @return the configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

}
