package backend.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import backend.api_gateway.routes.Routes;

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
}
