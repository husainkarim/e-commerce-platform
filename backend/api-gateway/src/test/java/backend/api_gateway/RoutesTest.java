package backend.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {
        "user.service.url=http://localhost:8081",
        "product.service.url=http://localhost:8082",
        "media.service.url=http://localhost:8083",
        "order.service.url=http://localhost:8084"
})
class RoutesTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldLoadUserRouteBean() {
        assertThat(context.getBean("userServiceRoute")).isNotNull();
    }

    @Test
    void shouldLoadProductRouteBean() {
        assertThat(context.getBean("productServiceRoute")).isNotNull();
    }

    @Test
    void shouldLoadMediaRouteBean() {
        assertThat(context.getBean("mediaServiceRoute")).isNotNull();
    }

    @Test
    void shouldLoadOrderRouteBean() {
        assertThat(context.getBean("orderServiceRoute")).isNotNull();
    }
}
