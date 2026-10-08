package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.usecase.DeactivateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateResponse;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
@Slf4j
public class CustomerController {
    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerInitiateResponse initiateCustomer(@RequestBody CustomerInitiateRequest customerInitiateRequest){
        InitiateCustomerResult initiatedCustomerResult=  initiateCustomerUseCase.execute(customerWebMapper.toCommand(customerInitiateRequest));
        log.info("InitiatedCustomerResult  : {}" , initiatedCustomerResult);

        return customerWebMapper.toResponse(initiatedCustomerResult);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCustomer(@PathVariable("id") String id) {
        deactivateCustomerUseCase.execute(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomerUpdateResponse updateCustomer(@PathVariable("id") String id, @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        CustomerId customerId = new CustomerId(UUID.fromString(id));
        return customerWebMapper.toCustomerUpdateResponse(updateCustomerUseCase.execute(customerId,customerWebMapper.toUpdateCustomerCommand(customerUpdateRequest)));
    }
}
