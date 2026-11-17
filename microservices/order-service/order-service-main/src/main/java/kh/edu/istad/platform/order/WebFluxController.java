package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.config.ServicePropConfig;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RefreshScope
@RestController
@RequiredArgsConstructor
public class WebFluxController {

    private final CustomerServiceClient customerServiceClient;

    @GetMapping("customers")
    public Flux<QueryCustomerResult> getCustomers(){
        return customerServiceClient.getCustomerNames();
    }


    @GetMapping("/flux")
    public Mono<Map<String, Object>> getDemo(@AuthenticationPrincipal Mono<Jwt> jwtMono) {
        return jwtMono
                .map(Jwt::getTokenValue)
                .defaultIfEmpty("No Token")
                .map(token -> Map.of(
                        "accessToken", token,
                        "message", "Hello from Order Service"
                ));
    }

//    @Value("${global.version}")
//    private String globalVersion;
//
//    private final ServicePropConfig servicePropConfig;
//
//    @GetMapping("/consul/config")
//    public Mono<Map<String, Object>> getConsulConfig() {
//        return Mono.just(
//                Map.of(
//                        "globalVersion", globalVersion,
//                        "message", servicePropConfig.getMessage(),
//                        "version", servicePropConfig.getVersion(),
//                        "env", servicePropConfig.getEnv(),
//                        "baseUrl", servicePropConfig.getBaseUrl())
//        );
//    }

}
