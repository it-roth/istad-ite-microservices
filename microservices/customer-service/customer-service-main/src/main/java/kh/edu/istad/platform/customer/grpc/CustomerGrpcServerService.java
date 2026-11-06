package kh.edu.istad.platform.customer.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.customer.v1.CreateCustomerRequest;
import kh.edu.istad.customer.v1.CreateCustomerResponse;
import kh.edu.istad.customer.v1.CustomerGrpcServiceGrpc;
import kh.edu.istad.customer.v1.GetCustomerByIdRequest;
import kh.edu.istad.customer.v1.GetCustomerByIdResponse;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerGrpcServerService extends CustomerGrpcServiceGrpc.CustomerGrpcServiceImplBase {

    private final QueryCustomerUseCase queryCustomerUseCase;

    @Override
    public void getCustomerById(GetCustomerByIdRequest request, StreamObserver<GetCustomerByIdResponse> responseObserver) {
        log.info("gRPC getCustomerById received for customerId: {}", request.getCustomerId());
        queryCustomerUseCase.findAll()
                .filter(c -> c.getId() != null && c.getId().value().toString().equals(request.getCustomerId()))
                .next()
                .subscribe(
                        customer -> {
                            responseObserver.onNext(toCustomerResponse(customer));
                            responseObserver.onCompleted();
                        },
                        error -> responseObserver.onError(Status.INTERNAL.withDescription(error.getMessage()).asRuntimeException()),
                        () -> responseObserver.onError(Status.NOT_FOUND.withDescription("Customer not found").asRuntimeException())
                );
    }

    @Override
    public void createCustomer(CreateCustomerRequest request, StreamObserver<CreateCustomerResponse> responseObserver) {
        log.info("gRPC createCustomer received for username: {}", request.getUsername());
        CreateCustomerResponse response = CreateCustomerResponse.newBuilder()
                .setCustomerId(java.util.UUID.randomUUID().toString())
                .setUsername(request.getUsername())
                .setFamilyName(request.getFamilyName())
                .setGivenName(request.getGivenName())
                .setEmail(request.getEmail())
                .setPhoneNumber(request.getPhoneNumber())
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GetCustomerByIdResponse toCustomerResponse(Customer c) {
        GetCustomerByIdResponse.Builder builder = GetCustomerByIdResponse.newBuilder();
        if (c.getId() != null && c.getId().value() != null) {
            builder.setCustomerId(c.getId().value().toString());
        }
        if (c.getUsername() != null) {
            builder.setUsername(c.getUsername());
        }
        if (c.getFamilyName() != null) {
            builder.setFamilyName(c.getFamilyName());
        }
        if (c.getGivenName() != null) {
            builder.setGivenName(c.getGivenName());
        }
        if (c.getEmail() != null && c.getEmail().value() != null) {
            builder.setEmail(c.getEmail().value());
        }
        if (c.getPhoneNumber() != null && c.getPhoneNumber().value() != null) {
            builder.setPhoneNumber(c.getPhoneNumber().value());
        }
        if (c.getStatus() != null) {
            builder.setStatus(c.getStatus().name());
        }
        return builder.build();
    }
}
