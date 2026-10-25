package kh.edu.istad.platform.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerApplicationService {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplicationService.class, args);
    }

}
