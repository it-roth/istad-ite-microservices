package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistics;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeactivateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeleteCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerRequest;
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
    private final CreateCustomerUseCase createCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final QueryCustomerUseCase queryCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@RequestBody CreateCustomerRequest request) {
        return createCustomerUseCase.create(customerWebMapper.toDomain(request));
    }

    @GetMapping
    public Flux<Customer> findAll(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "familyName", required = false) String familyName,
            @RequestParam(name = "givenName", required = false) String givenName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "status", required = false) CustomerStatus status,
            @RequestParam(name = "segmentId", required = false) String segmentId
    ) {
        return queryCustomerUseCase.findByFilter(new CustomerFilter(
                username, familyName, givenName, email, status, segmentId
        ));
    }

    @GetMapping("/statistics")
    public Flux<CustomerStatistics> statistics() {
        return queryCustomerUseCase.statistics();
    }

    @GetMapping("/{id}")
    public Mono<Customer> findById(@PathVariable("id") String id) {
        return queryCustomerUseCase.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<Customer> update(
            @PathVariable("id") String id,
            @RequestBody CreateCustomerRequest request
    ) {
        return updateCustomerUseCase.update(id, customerWebMapper.toDomain(request));
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<Customer> deactivate(@PathVariable("id") String id) {
        return deactivateCustomerUseCase.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return deleteCustomerUseCase.delete(id);
    }
}
