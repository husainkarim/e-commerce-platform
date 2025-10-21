package backend.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, not recommended for production
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll() // allow access without login
                .requestMatchers(HttpMethod.OPTIONS).permitAll() // allow product viewing without auth
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll() // allow login without auth
                .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll() // allow registration without auth
                .anyRequest().authenticated() // require auth for everything else
            )
            .formLogin(form -> form.disable())  // Disable default login form
            .httpBasic(basic -> basic.disable()); // Disable basic auth too, for now

        return http.build();
    }
}
