package backend.api_gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${media.service.url}")
    private String mediaServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service
                .route("user-service", r -> r.path("/api/users/**")
                        .uri(userServiceUrl))
                // Product Service
                .route("product-service", r -> r.path("/api/products/**")
                        .uri(productServiceUrl))
                // Media Service
                .route("media-service", r -> r.path("/api/media/**")
                        .uri(mediaServiceUrl))
                // Order Service
                .route("order-service", r -> r.path("/api/orders/**")
                        .uri(orderServiceUrl))
                .build();
    }
}