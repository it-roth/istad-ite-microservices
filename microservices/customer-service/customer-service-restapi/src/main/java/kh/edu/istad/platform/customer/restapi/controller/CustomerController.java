package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final QueryCustomerUseCase queryCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @GetMapping
    public Flux<Customer> findAll(){
        return queryCustomerUseCase.findAll();
    }

    @GetMapping("/query")
    public Flux<Customer> query(){
        return queryCustomerUseCase.query();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@RequestBody CreateCustomerRequest request){
        return createCustomerUseCase.create(customerWebMapper.toDomain(request));
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    public CustomerInitiateResponse initiateCustomer(
//            @RequestBody CustomerInitiateRequest customerInitiateRequest
//            ) {
//
//        InitiateCustomerResult result = initiateCustomerUseCase.execute(
//                customerWebMapper.toCommand(customerInitiateRequest)
//        );
//        return customerWebMapper.toResponse(result);
//    }

}
