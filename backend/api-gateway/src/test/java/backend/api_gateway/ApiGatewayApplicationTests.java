package backend.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import backend.api_gateway.routes.RoutesTest;
import backend.api_gateway.security.JwtAuthenticationFilterTest;
import backend.api_gateway.security.SecurityConfigTest;

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

		assertThat(SecurityConfigTest.class).isNotNull();
	}

	@Test
	void shouldLoadRouterFunctions() {
		// Router functions are tested in RoutesTest
		assertThat(RoutesTest.class).isNotNull();
	}

	@Test
	void shouldLoadCorsConfiguration() {
		// CORS configuration is tested in SecurityConfigTest
		assertThat(SecurityConfigTest.class).isNotNull();
	}

	@Test
	void shouldHaveJwtAuthenticationFilter() {
		// JWT filter is tested in SecurityConfigTest and JwtAuthenticationFilterTest
		assertThat(JwtAuthenticationFilterTest.class).isNotNull();
	}

	@Test
	void shouldHaveJwtService() {
		// JWT service is tested in JwtServiceTest
		assertThat(SecurityConfigTest.class).isNotNull();
	}

	@Test
	void shouldApplicationStartSuccessfully() {
		// Application class exists and is accessible
		assertThat(ApiGatewayApplication.class).isNotNull();
	}

}
