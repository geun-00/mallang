package io.mallang.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/members").permitAll()

                        .anyRequest().authenticated()
                )

                .build();
    }
}
