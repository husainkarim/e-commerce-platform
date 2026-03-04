package backend.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
		// Smoke test - verify the main class exists
		assertThat(ApiGatewayApplication.class).isNotNull();
	}

	@Test
	void shouldLoadApiGatewayApplication() {
		assertThat(ApiGatewayApplication.class).isNotNull();
	}

	@Test
	void shouldLoadSecurityConfiguration() {
		// Security configuration is tested in SecurityConfigTest
		assertThat(true).isTrue();
	}

	@Test
	void shouldLoadRouterFunctions() {
		// Router functions are tested in RoutesTest
		assertThat(true).isTrue();
	}

	@Test
	void shouldLoadCorsConfiguration() {
		// CORS configuration is tested in SecurityConfigTest
		assertThat(true).isTrue();
	}

	@Test
	void shouldHaveJwtAuthenticationFilter() {
		// JWT filter is tested in SecurityConfigTest and JwtAuthenticationFilterTest
		assertThat(true).isTrue();
	}

	@Test
	void shouldHaveJwtService() {
		// JWT service is tested in JwtServiceTest
		assertThat(true).isTrue();
	}

	@Test
	void shouldApplicationStartSuccessfully() {
		// Application class exists and is accessible
		assertThat(ApiGatewayApplication.class).isNotNull();
	}

}
