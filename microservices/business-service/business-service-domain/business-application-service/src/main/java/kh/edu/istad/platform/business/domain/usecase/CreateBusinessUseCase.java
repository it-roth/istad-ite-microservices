package kh.edu.istad.platform.business.domain.usecase;

import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.event.BusinessRegisteredEvent;
import kh.edu.istad.platform.business.domain.mapper.BusinessDataMapper;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateBusinessUseCase {

    private final BusinessDomainService businessDomainService;
    private final BusinessDataMapper businessDataMapper;
    private final BusinessRepository businessRepository;

    public Mono<CreateBusinessResult> execute(CreateBusinessCommand command) {
        Business business = businessDataMapper.createBusinessCommandToBusiness(command);
        BusinessRegisteredEvent businessRegisteredEvent = businessDomainService.validateAndInitiateBusiness(business);

        return businessRepository.save(business)
                .doOnSuccess(savedBusiness -> log.info("Business created with id: {}", savedBusiness.getId().value()))
                .map(savedBusiness -> businessDataMapper.businessToCreateBusinessResult(
                        savedBusiness,
                        "Business registered successfully"
                ));
    }
}
