package kh.edu.istad.platform.business.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.business.domain.usecase.CreateBusinessUseCase;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessRequest;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessResponse;
import kh.edu.istad.platform.business.restapi.mapper.BusinessWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/businesses")
@Slf4j
@RequiredArgsConstructor
public class BusinessController {

    private final CreateBusinessUseCase createBusinessUseCase;
    private final BusinessWebMapper businessWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateBusinessResponse> createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        log.info("Creating business: {}", request.businessName());

        var command = businessWebMapper.toCommand(request);
        return createBusinessUseCase.execute(command)
                .map(businessWebMapper::toResponse)
                .doOnSuccess(response -> log.info("Business created with id: {}", response.businessId()));
    }
}
