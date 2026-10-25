package kh.edu.istad.platform.business.restapi.mapper;

import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessRequest;
import kh.edu.istad.platform.business.restapi.dto.CreateBusinessResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BusinessWebMapper {

    public CreateBusinessCommand toCommand(CreateBusinessRequest request) {
        return new CreateBusinessCommand(
                request.businessName(),
                request.email(),
                request.phoneNumber(),
                request.products().stream()
                        .map(p -> new CreateBusinessCommand.ProductCommand(
                                p.name(),
                                p.price(),
                                p.quantity(),
                                p.available()
                        ))
                        .collect(Collectors.toList())
        );
    }

    public CreateBusinessResponse toResponse(CreateBusinessResult result) {
        return new CreateBusinessResponse(
                result.businessId(),
                result.businessName(),
                result.message()
        );
    }
}
