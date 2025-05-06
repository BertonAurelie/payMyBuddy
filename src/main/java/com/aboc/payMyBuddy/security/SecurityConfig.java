package com.aboc.payMyBuddy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println(http);
        http.authorizeHttpRequests((auth) -> {
            auth.anyRequest().permitAll();
        });
        http.csrf(AbstractHttpConfigurer::disable); //UTILISATION TEMPORAIRE - A supprimer
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 is the cost factor
    }
}

//@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/**").permitAll()
//                        .anyRequest().denyAll()) // deny everything except for the above
//                .httpBasic(withDefaults());
//        return http.build();
//    }



