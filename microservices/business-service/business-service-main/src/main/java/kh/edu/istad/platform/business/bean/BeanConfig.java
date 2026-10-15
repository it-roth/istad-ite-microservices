package kh.edu.istad.platform.business.bean;

import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import kh.edu.istad.platform.business.domain.service.BusinessDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public BusinessDomainService businessDomainService() {
        return new BusinessDomainServiceImpl();
    }
}
