package kh.edu.istad.platform.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "kh.edu.istad")
@EnableReactiveMongoRepositories(basePackages = "kh.edu.istad.platform.customer.persistence.repository")
public class CustomerApplicationService {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplicationService.class, args);
    }
}