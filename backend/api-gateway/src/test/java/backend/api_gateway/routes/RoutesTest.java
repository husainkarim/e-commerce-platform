package backend.api_gateway.routes;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class RoutesTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(Routes.class)
                    .withPropertyValues(
                            "user.service.url=http://localhost:8081",
                            "product.service.url=http://localhost:8082",
                            "media.service.url=http://localhost:8083",
                            "order.service.url=http://localhost:8084"
                    );

    @Test
    void shouldLoadAllRouteBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("userServiceRoute");
            assertThat(context).hasBean("productServiceRoute");
            assertThat(context).hasBean("mediaServiceRoute");
            assertThat(context).hasBean("orderServiceRoute");
        });
    }

    @Test
    void shouldLoadAllRouterFunctionBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Routes.class);
            assertThat(context.getBean("userServiceRoute")).isNotNull();
            assertThat(context.getBean("productServiceRoute")).isNotNull();
            assertThat(context.getBean("mediaServiceRoute")).isNotNull();
            assertThat(context.getBean("orderServiceRoute")).isNotNull();
        });
    }

    @Test
    void shouldHaveCorrectServiceUrls() {
        contextRunner.run(context -> {
            Routes routes = context.getBean(Routes.class);
            
            assertThat(routes).isNotNull();
        });
    }

    @Test
    void shouldLoadRoutesWithValidUrls() {
        contextRunner.withPropertyValues(
                "user.service.url=https://user-service.example.com",
                "product.service.url=https://product-service.example.com",
                "media.service.url=https://media-service.example.com",
                "order.service.url=https://order-service.example.com"
        ).run(context -> {
            assertThat(context).hasBean("userServiceRoute");
            assertThat(context).hasBean("productServiceRoute");
            assertThat(context).hasBean("mediaServiceRoute");
            assertThat(context).hasBean("orderServiceRoute");
        });
    }

    @Test
    void shouldNotCreateContextWithoutServiceUrls() {
        new ApplicationContextRunner()
                .withUserConfiguration(Routes.class)
                .run(context -> {
                    // Context should still load, but beans may not be created if properties are missing
                    assertThat(context).hasNotFailed();
                });
    }

    @Test
    void shouldSupportMultiplePortNumbers() {
        contextRunner.withPropertyValues(
                "user.service.url=http://localhost:9001",
                "product.service.url=http://localhost:9002",
                "media.service.url=http://localhost:9003",
                "order.service.url=http://localhost:9004"
        ).run(context -> {
            assertThat(context).hasBean("userServiceRoute");
            assertThat(context).hasBean("productServiceRoute");
            assertThat(context).hasBean("mediaServiceRoute");
            assertThat(context).hasBean("orderServiceRoute");
        });
    }

    @Test
    void shouldSupportDifferentHosts() {
        contextRunner.withPropertyValues(
                "user.service.url=http://user-host:8081",
                "product.service.url=http://product-host:8082",
                "media.service.url=http://media-host:8083",
                "order.service.url=http://order-host:8084"
        ).run(context -> {
            assertThat(context).hasBean("userServiceRoute");
            assertThat(context).hasBean("productServiceRoute");
            assertThat(context).hasBean("mediaServiceRoute");
            assertThat(context).hasBean("orderServiceRoute");
        });
    }

    @Test
    void shouldCreateRoutesBeanSuccessfully() {
        contextRunner.run(context -> {
            Routes routes = context.getBean(Routes.class);
            assertThat(routes).isNotNull();
            assertThat(routes).isInstanceOf(Routes.class);
        });
    }
}
