package kh.edu.istad.platform.iam.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@Configuration
@ConditionalOnProperty(name = "spring.session.store-type", havingValue = "redis", matchIfMissing = true)
@EnableRedisIndexedHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "spring:session:iam")
public class RedisSessionConfig {

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
