package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.dto.CustomerServiceResponse;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {
    private final WebClient directWebClient;
    private final WebClient loadBalancedWebClient;

    @Value("${microservices.hardcoded.customer-url:http://localhost:9091}")
    private String hardcodedCustomerUrl;

    @Value("${microservices.eureka.customer-url:http://customer-service}")
    private String eurekaCustomerUrl;

    public CustomerRepositoryAdapter(
            @Qualifier("directWebClient") WebClient directWebClient,
            @Qualifier("loadBalancedWebClient") WebClient loadBalancedWebClient
    ) {
        this.directWebClient = directWebClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public CustomerServiceResponse findCustomer(CustomerId customerId, String discoveryMode) {
        boolean useEureka = !"HARD_CODED".equalsIgnoreCase(discoveryMode);
        String baseUrl = useEureka ? eurekaCustomerUrl : hardcodedCustomerUrl;
        CustomerServiceResponse customer = (useEureka ? loadBalancedWebClient : directWebClient)
                .get()
                .uri(baseUrl + "/api/customers/" + customerId.value())
                .retrieve()
                .bodyToMono(CustomerServiceResponse.class)
                .block(Duration.ofSeconds(4));

        if (customer == null || customer.id() == null
                || !customerId.value().equals(customer.id().value())) {
            throw new OrderDomainException("Customer service returned no matching customer");
        }
        if (!"ACTIVE".equalsIgnoreCase(customer.status())) {
            throw new OrderDomainException("Customer is not active: " + customerId.value());
        }
        return customer;
    }
}
