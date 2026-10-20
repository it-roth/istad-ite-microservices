package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeleteCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.restapi.dto.CustomerSegmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer-segments")
public class CustomerSegmentController {
    private final CreateCustomerSegmentUseCase createCustomerSegmentUseCase;
    private final QueryCustomerSegmentUseCase queryCustomerSegmentUseCase;
    private final UpdateCustomerSegmentUseCase updateCustomerSegmentUseCase;
    private final DeleteCustomerSegmentUseCase deleteCustomerSegmentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerSegment> create(@RequestBody CustomerSegmentRequest request) {
        return createCustomerSegmentUseCase.create(
                CustomerSegment.builder().type(request.type()).build()
        );
    }

    @GetMapping
    public Flux<CustomerSegment> findAll() {
        return queryCustomerSegmentUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CustomerSegment> findById(@PathVariable("id") String id) {
        return queryCustomerSegmentUseCase.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<CustomerSegment> update(
            @PathVariable("id") String id,
            @RequestBody CustomerSegmentRequest request
    ) {
        return updateCustomerSegmentUseCase.update(
                id,
                CustomerSegment.builder().type(request.type()).build()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return deleteCustomerSegmentUseCase.delete(id);
    }
}
