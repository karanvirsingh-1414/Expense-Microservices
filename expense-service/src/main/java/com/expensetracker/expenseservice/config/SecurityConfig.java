package com.expensetracker.expenseservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/dashboard", "/expense", "/expense/**", "/category/**", "/css/**", "/js/**",
                                "/static/**",
                                "/api/expense", "/api/expenses/**",
                                "/salary", "/login", "/logout")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin().disable()
                .httpBasic().disable()
                .logout().permitAll();
        return http.build();
    }
}
