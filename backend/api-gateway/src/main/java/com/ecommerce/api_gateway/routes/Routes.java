package com.ecommerce.api_gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {
    @Value("${user.service.url}")
	private String userServiceUrl;
    @Value("${product.service.url}")
	private String productServiceUrl;
    @Value("${media.service.url}")
	private String mediaServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
            .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http(userServiceUrl))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product-service")
            .route(RequestPredicates.path("/api/products/**"), HandlerFunctions.http(productServiceUrl))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> mediaServiceRoute() {
        return GatewayRouterFunctions.route("media-service")
            .route(RequestPredicates.path("/api/media/**"), HandlerFunctions.http(mediaServiceUrl))
            .build();
    }
}
