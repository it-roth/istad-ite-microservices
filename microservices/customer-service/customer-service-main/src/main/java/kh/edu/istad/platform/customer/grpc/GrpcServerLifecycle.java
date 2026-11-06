package kh.edu.istad.platform.customer.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServerLifecycle {

    private final CustomerGrpcServerService customerGrpcServerService;
    private Server server;

    @Value("${grpc.server.port:9094}")
    private int grpcPort;

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(grpcPort)
                .addService(customerGrpcServerService)
                .build()
                .start();
        log.info("Started gRPC server on port {}", grpcPort);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            log.info("Stopping gRPC server...");
            server.shutdown();
        }
    }
}
