package com.cinema.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cinema.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configure authorization based on PDF requirements
                .authorizeHttpRequests(auth -> auth
                // Admin-only URLs
                .requestMatchers("/admin/panel/halls").hasRole("ADMIN")
                .requestMatchers("/admin/panel/films").hasRole("ADMIN")
                .requestMatchers("/admin/panel/sessions").hasRole("ADMIN")
                // Any authorized user URLs
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/session/search").authenticated()
                .requestMatchers("/films/*/chat/messages").authenticated()
                .requestMatchers("/films/*/chat").authenticated()
                // Public URLs (signIn, signUp)
                .requestMatchers("/signIn", "/signUp").permitAll()
                .requestMatchers("/test/**").permitAll() // Test endpoints
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Static resources

                // All other requests require authentication
                .anyRequest().authenticated()
                )
                // Configure form login with custom login page
                .formLogin(form -> form
                .loginPage("/signIn")
                .loginProcessingUrl("/signIn")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/profile", true)
                .failureUrl("/signIn?error=true")
                .permitAll()
                )
                // Configure logout
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/signIn?logout=true")
                .permitAll()
                )
                // Configure remember-me functionality
                .rememberMe(remember -> remember
                .key("cinema-remember-me-key")
                .tokenValiditySeconds(86400) // 24 hours
                .userDetailsService(customUserDetailsService)
                )
                // Enable CSRF protection (required by PDF)
                // CSRF is now enabled by default

                // Set our custom UserDetailsService
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}
