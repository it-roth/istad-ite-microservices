package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.BusinessServiceResponse;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
public class BusinessRepositoryAdapter implements BusinessRepository {
    private final WebClient directWebClient;
    private final WebClient loadBalancedWebClient;

    @Value("${microservices.hardcoded.business-url:http://localhost:9093}")
    private String hardcodedBusinessUrl;

    @Value("${microservices.eureka.business-url:http://business-service}")
    private String eurekaBusinessUrl;

    public BusinessRepositoryAdapter(
            @Qualifier("directWebClient") WebClient directWebClient,
            @Qualifier("loadBalancedWebClient") WebClient loadBalancedWebClient
    ) {
        this.directWebClient = directWebClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public Business findBusinessInformation(Business requestedBusiness, String discoveryMode) {
        boolean useEureka = !"HARD_CODED".equalsIgnoreCase(discoveryMode);
        String baseUrl = useEureka ? eurekaBusinessUrl : hardcodedBusinessUrl;
        UUID requestedBusinessId = requestedBusiness.getId().value();
        BusinessServiceResponse response = (useEureka ? loadBalancedWebClient : directWebClient)
                .get()
                .uri(baseUrl + "/api/businesses/" + requestedBusinessId)
                .retrieve()
                .bodyToMono(BusinessServiceResponse.class)
                .block(Duration.ofSeconds(4));

        if (response == null || response.id() == null
                || !requestedBusinessId.equals(response.id().value())) {
            throw new OrderDomainException("Business service returned no matching business");
        }
        if (!response.active()) {
            throw new OrderDomainException("Business is not active: " + requestedBusinessId);
        }

        List<BusinessServiceResponse.ProductResponse> catalog =
                response.products() == null ? List.of() : response.products();
        List<Product> confirmedProducts = requestedBusiness.getProducts().stream()
                .map(requestedProduct -> findConfirmedProduct(requestedProduct, catalog))
                .toList();
        return Business.builder()
                .businessId(requestedBusiness.getId())
                .products(confirmedProducts)
                .active(response.active())
                .businessName(response.businessName())
                .build();
    }

    private Product findConfirmedProduct(
            Product requestedProduct,
            List<BusinessServiceResponse.ProductResponse> catalog
    ) {
        UUID requestedProductId = requestedProduct.getId().value();
        BusinessServiceResponse.ProductResponse product = catalog.stream()
                .filter(candidate -> candidate.id() != null
                        && requestedProductId.equals(candidate.id().value()))
                .findFirst()
                .orElseThrow(() -> new OrderDomainException(
                        "Product does not belong to this business: " + requestedProductId));

        if (!product.available()) {
            throw new OrderDomainException("Product is unavailable: " + requestedProductId);
        }
        if (product.name() == null || product.name().isBlank()
                || product.price() == null || product.price().amount() == null) {
            throw new OrderDomainException("Business returned incomplete product data: " + requestedProductId);
        }
        return Product.builder()
                .id(new ProductId(product.id().value()))
                .name(product.name())
                .price(new Money(product.price().amount()))
                .build();
    }
}
