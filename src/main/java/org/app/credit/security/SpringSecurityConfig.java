package org.app.credit.security;

import org.app.credit.security.filter.JwtAuthenticationFilter;
import org.app.credit.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final TokenJwtConfig tokenJwtConfig;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    public SpringSecurityConfig(TokenJwtConfig tokenJwtConfig) {
        this.tokenJwtConfig = tokenJwtConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CAMBIO AQUÍ: Cambié el nombre del método de 'springSecurityFilterChain' a 'filterChain'
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authManager = authenticationConfiguration.getAuthenticationManager();
        return http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/requests/**").hasAnyRole("CLIENT", "ANALYST")
                        .requestMatchers(HttpMethod.GET, "/requests/evaluate/**").hasRole("ANALYST")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authManager, tokenJwtConfig))
                .addFilter(new JwtValidationFilter(authManager, tokenJwtConfig))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}