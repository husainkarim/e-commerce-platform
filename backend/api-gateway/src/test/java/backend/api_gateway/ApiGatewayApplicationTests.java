package backend.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}

	@Test
	void shouldLoadApiGatewayApplication() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("apiGatewayApplication")).isTrue();
	}

	@Test
	void shouldLoadSecurityConfiguration() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("securityFilterChain")).isTrue();
	}

	@Test
	void shouldLoadRouterFunctions() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("userServiceRoute")).isTrue();
		assertThat(applicationContext.containsBean("productServiceRoute")).isTrue();
		assertThat(applicationContext.containsBean("mediaServiceRoute")).isTrue();
		assertThat(applicationContext.containsBean("orderServiceRoute")).isTrue();
	}

	@Test
	void shouldLoadCorsConfiguration() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("corsConfigurationSource")).isTrue();
	}

	@Test
	void shouldHaveJwtAuthenticationFilter() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("jwtAuthenticationFilter")).isTrue();
	}

	@Test
	void shouldHaveJwtService() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.containsBean("jwtService")).isTrue();
	}

	@Test
	void shouldApplicationStartSuccessfully() {
		assertThat(applicationContext).isNotNull();
	}

}
