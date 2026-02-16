package backend.api_gateway.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.cors.CorsConfigurationSource;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadSecurityConfiguration() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("securityFilterChain")).isTrue();
    }

    @Test
    void shouldLoadCorsConfigurationSource() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("corsConfigurationSource")).isTrue();
    }

    @Test
    void shouldHaveSecurityFilterChainBean() {
        SecurityFilterChain chain = applicationContext.getBean(SecurityFilterChain.class);
        assertThat(chain).isNotNull();
    }

    @Test
    void shouldHaveCorsConfigurationSourceBean() {
        assertThat(applicationContext).isNotNull();
        String[] beanNames = applicationContext.getBeanNamesForType(CorsConfigurationSource.class);
        assertThat(beanNames).isNotEmpty();
        // Verify that corsConfigurationSource bean exists
        assertThat(applicationContext.containsBean("corsConfigurationSource")).isTrue();
    }

    @Test
    void shouldLoadJwtAuthenticationFilter() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("jwtAuthenticationFilter")).isTrue();
    }

    @Test
    void shouldLoadJwtService() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("jwtService")).isTrue();
    }

    @Test
    void shouldHaveSecurityConfigurationBeanWithCorrectType() {
        assertThat(applicationContext).isNotNull();
        SecurityConfig config = applicationContext.getBean(SecurityConfig.class);
        assertThat(config).isNotNull();
        assertThat(config).isInstanceOf(SecurityConfig.class);
    }

    @Test
    void shouldLoadSecurityBeansSuccessfully() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBeansOfType(SecurityFilterChain.class)).isNotEmpty();
        assertThat(applicationContext.getBeansOfType(CorsConfigurationSource.class)).isNotEmpty();
    }
}
