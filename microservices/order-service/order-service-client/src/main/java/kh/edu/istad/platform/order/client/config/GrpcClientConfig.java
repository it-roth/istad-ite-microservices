package kh.edu.istad.platform.order.client.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kh.edu.istad.customer.v1.CustomerGrpcServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.customer-service.host:localhost}")
    private String host;

    @Value("${grpc.client.customer-service.port:9094}")
    private int port;

    @Bean
    public ManagedChannel customerServiceChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public CustomerGrpcServiceGrpc.CustomerGrpcServiceBlockingStub customerGrpcServiceBlockingStub(ManagedChannel customerServiceChannel) {
        return CustomerGrpcServiceGrpc.newBlockingStub(customerServiceChannel);
    }
}
