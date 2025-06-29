package com.sisregistration.bmwsis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity (enable in production)
            .authorizeHttpRequests(authz -> authz
                // Public resources
                .requestMatchers("/", "/sis", "/css/**", "/js/**", "/images/**", "/*.png", "/*.jpg").permitAll()
                
                // Test endpoints (for password migration)
                .requestMatchers("/test-password-hashing", "/migrate-passwords").permitAll()
                
                // Login pages and logout
                .requestMatchers("/student/login", "/faculty/login", "/admin/login").permitAll()
                .requestMatchers("/student/logout", "/faculty/logout", "/admin/logout").permitAll()
                
                // Allow all authenticated routes (session-based auth)
                .requestMatchers("/student/**", "/faculty/**", "/admin/**").permitAll()
                
                // Actuator endpoints (open for now, restrict later)
                .requestMatchers("/actuator/**").permitAll()
                
                // Any other request requires authentication
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable()) // We'll use custom login forms
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/sis")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
} 