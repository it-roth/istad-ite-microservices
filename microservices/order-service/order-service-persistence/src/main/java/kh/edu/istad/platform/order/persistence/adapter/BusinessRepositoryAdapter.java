package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Slf4j
public class BusinessRepositoryAdapter implements BusinessRepository {

    @Override
    public Business findBusinessInformation(Business business) {
        // STUB: Return a mock business with products
        log.info("Finding business information for business id: {}", business.getId().value());

        List<Product> products = business.getProducts().stream()
                .map(requestedProduct -> Product.builder()
                        .id(requestedProduct.getId())
                        .name("Product " + requestedProduct.getId().value())
                        .price(new Money(BigDecimal.valueOf(100.00)))
                        .build())
                .toList();

        return Business.builder()
                .businessId(business.getId())
                .products(products)
                .active(true)
                .build();
    }
}
