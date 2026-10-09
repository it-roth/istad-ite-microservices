package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
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
    public CustomerInitiateResponse initiateCustomer(@Valid @RequestBody CustomerInitiateRequest customerInitiateRequest) {
        InitiateCustomerResult initiateCustomerResult = initiateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerInitiateRequest)
        );
        log.info("InitiateCustomerResult : {}", initiateCustomerResult);

        return customerWebMapper.toResponse(initiateCustomerResult);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerUpdateResponse updateCustomer(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest
    ) {
        CustomerId customerId = new CustomerId(id);
        return customerWebMapper.toCustomerUpdateResponse(
                updateCustomerUseCase.execute(customerId, customerWebMapper.toUpdateCustomerCommand(customerUpdateRequest))
        );
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCustomer(@PathVariable("id") UUID id) {
        CustomerId customerId = new CustomerId(id);
        deactivateCustomerUseCase.execute(customerId.value().toString());
    }
}