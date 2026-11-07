package kh.edu.istad.platform.iam.config;

import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.service.IamDomainServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;


@Configuration
public class IamConfig {

    @Bean
    public IamDomainService iamDomainService() {
        return new IamDomainServiceImpl();
    }
}
