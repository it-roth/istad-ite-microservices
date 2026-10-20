package kh.edu.istad.platform.customer.persistence.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

@Configuration
public class AuditAwareConfig implements ReactiveAuditorAware<String> {
    @Override
    public Mono<String> getCurrentAuditor() {
        return Mono.just("SYSTEM");
    }
}
