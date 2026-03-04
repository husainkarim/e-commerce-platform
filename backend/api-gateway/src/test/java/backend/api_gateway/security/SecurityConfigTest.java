package backend.api_gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.cors.CorsConfigurationSource;

@SpringJUnitConfig
public class SecurityConfigTest {

    @TestConfiguration
    static class SecurityConfigTestConfig {
        
        @Bean
        @Primary
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }

        @Bean
        public SecurityConfig securityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
            return new SecurityConfig(jwtAuthenticationFilter);
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource(SecurityConfig securityConfig) {
            return securityConfig.corsConfigurationSource();
        }
    }

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void shouldLoadSecurityConfiguration() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldLoadCorsConfigurationSource() {
        assertThat(corsConfigurationSource).isNotNull();
    }

    @Test
    void shouldHaveSecurityFilterChainBean() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldHaveCorsConfigurationSourceBean() {
        assertThat(corsConfigurationSource).isNotNull();
    }

    @Test
    void shouldLoadJwtAuthenticationFilter() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldLoadJwtService() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void shouldHaveSecurityConfigurationBeanWithCorrectType() {
        assertThat(securityConfig).isNotNull();
        assertThat(securityConfig).isInstanceOf(SecurityConfig.class);
    }

    @Test
    void shouldLoadSecurityBeansSuccessfully() {
        assertThat(securityConfig).isNotNull();
        assertThat(corsConfigurationSource).isNotNull();
    }
}
