package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.customer.v1.CustomerGrpcServiceGrpc;
import kh.edu.istad.customer.v1.GetCustomerByIdRequest;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    private final CustomerGrpcServiceGrpc.CustomerGrpcServiceBlockingStub customerGrpcServiceBlockingStub;

    @Override
    public Flux<QueryCustomerResult> getCustomerNames() {
        return Flux.defer(() -> {
            log.info("Calling customer-service via CustomerGrpcService.getCustomerById()");
            try {
                var response = customerGrpcServiceBlockingStub.getCustomerById(
                        GetCustomerByIdRequest.newBuilder()
                                .setCustomerId("test-customer-id")
                                .build()
                );

                return Flux.just(new QueryCustomerResult(
                        response.getFamilyName(),
                        response.getGivenName()
                ));
            } catch (Exception e) {
                log.warn("Customer gRPC call returned: {}", e.getMessage());
                return Flux.empty();
            }
        });
    }
}
