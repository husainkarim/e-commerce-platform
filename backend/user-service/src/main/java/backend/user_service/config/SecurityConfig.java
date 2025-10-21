package backend.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ❌ Disable CSRF for REST APIs
            .cors(cors -> {}) // ✅ Enable CORS (gateway handles it)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ No sessions
            )
            .formLogin(form -> form.disable()) // ❌ Disable default /login form
            .httpBasic(basic -> basic.disable()) // ❌ Disable Basic Auth
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔒 Others require auth (JWT)
            );

        return http.build();
    }
}
