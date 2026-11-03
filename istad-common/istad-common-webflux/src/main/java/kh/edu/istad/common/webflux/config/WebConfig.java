package kh.edu.istad.common.webflux.config;

import kh.edu.istad.common.webflux.exception.DomainExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class WebConfig {

    @Bean
    public DomainExceptionHandler domainExceptionHandler() {
        return new DomainExceptionHandler();
    }

}
