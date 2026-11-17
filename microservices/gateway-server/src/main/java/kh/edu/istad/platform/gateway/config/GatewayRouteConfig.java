package kh.edu.istad.platform.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("nextjs", r -> r.order(Ordered.LOWEST_PRECEDENCE)
                        .path("/**")
                        .and()
                        .not(p -> p.path("/scalar/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"))
                        .filters(f -> f.saveSession().tokenRelay())
                        .uri("http://localhost:3000"))
                .build();
    }


}

