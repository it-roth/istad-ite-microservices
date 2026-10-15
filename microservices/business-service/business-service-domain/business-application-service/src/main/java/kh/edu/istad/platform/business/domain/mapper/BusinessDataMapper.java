package kh.edu.istad.platform.business.domain.mapper;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.CreateBusinessResult;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BusinessDataMapper {

    public Business createBusinessCommandToBusiness(CreateBusinessCommand command) {
        return Business.builder()
                .businessName(command.businessName())
                .businessContact(new BusinessContact(
                        command.email(),
                        command.phoneNumber()
                ))
                .products(productsCommandToProducts(command.products()))
                .build();
    }

    public CreateBusinessResult businessToCreateBusinessResult(Business business, String message) {
        return new CreateBusinessResult(
                business.getId().value(),
                business.getBusinessName(),
                message
        );
    }

    private List<Product> productsCommandToProducts(List<CreateBusinessCommand.ProductCommand> products) {
        return products.stream()
                .map(productCommand -> Product.builder()
                        .productId(new ProductId(UUID.randomUUID()))
                        .name(productCommand.name())
                        .price(new Money(productCommand.price()))
                        .quantity(productCommand.quantity())
                        .available(productCommand.available())
                        .build())
                .collect(Collectors.toList());
    }
}
