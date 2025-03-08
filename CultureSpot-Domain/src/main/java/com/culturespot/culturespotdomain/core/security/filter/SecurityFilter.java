package com.culturespot.culturespotdomain.core.security.filter;

import com.culturespot.culturespotdomain.core.security.endpoint.EndpointAccessType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilter {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    EndpointAccessType.PUBLIC.getEndpoints()
                            .forEach(url -> auth.requestMatchers(url).permitAll());
                    EndpointAccessType.USER.getEndpoints()
                            .forEach(url -> auth.requestMatchers(url).authenticated());
                    EndpointAccessType.ADMIN.getEndpoints()
                            .forEach(url -> auth.requestMatchers(url).hasRole("ADMIN"));

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
