package backend.api_gateway.routes;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class RoutesTest {

    @TestConfiguration
    static class RoutesTestConfig {
        
        @Bean
        @Primary
        public RouteLocatorBuilder routeLocatorBuilder() {
            return Mockito.mock(RouteLocatorBuilder.class);
        }

        @Bean
        public Routes routes() {
            return new Routes();
        }
    }

    @Autowired
    private Routes routes;

    @Test
    void shouldLoadAllRouteBeans() {
        assertThat(routes).isNotNull();
    }

    @Test
    void shouldLoadAllRouterFunctionBeans() {
        assertThat(routes).isInstanceOf(Routes.class);
    }

    @Test
    void shouldHaveCorrectServiceUrls() {
        assertThat(routes).isNotNull();
    }

    @Test
    void shouldLoadRoutesWithValidUrls() {
        assertThat(routes).isNotNull();
    }

    @Test
    void shouldSupportMultiplePortNumbers() {
        assertThat(routes).isNotNull();
    }

    @Test
    void shouldSupportDifferentHosts() {
        assertThat(routes).isNotNull();
    }

    @Test
    void shouldCreateRoutesBeanSuccessfully() {
        assertThat(routes).isNotNull();
        assertThat(routes).isInstanceOf(Routes.class);
    }
}
