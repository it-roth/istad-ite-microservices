package kh.edu.istad.platform.business.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.persistence.document.BusinessContactDocument;
import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import kh.edu.istad.platform.business.persistence.document.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BusinessPersistenceMapper {

    public BusinessDocument toDocument(Business business) {
        return BusinessDocument.builder()
                .id(business.getId().value().toString())
                .businessName(business.getBusinessName())
                .businessContact(BusinessContactDocument.builder()
                        .email(business.getBusinessContact().email())
                        .phoneNumber(business.getBusinessContact().phoneNumber())
                        .build())
                .products(productsToDocuments(business.getProducts()))
                .active(business.isActive())
                .build();
    }

    public Business toAggregate(BusinessDocument document) {
        return Business.builder()
                .businessId(new BusinessId(UUID.fromString(document.getId())))
                .businessName(document.getBusinessName())
                .businessContact(new BusinessContact(
                        document.getBusinessContact().getEmail(),
                        document.getBusinessContact().getPhoneNumber()
                ))
                .products(documentsToProducts(document.getProducts()))
                .active(document.isActive())
                .build();
    }

    private List<ProductDocument> productsToDocuments(List<Product> products) {
        return products.stream()
                .map(product -> ProductDocument.builder()
                        .id(product.getId().value().toString())
                        .name(product.getName())
                        .price(product.getPrice().amount())
                        .quantity(product.getQuantity())
                        .available(product.isAvailable())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Product> documentsToProducts(List<ProductDocument> documents) {
        return documents.stream()
                .map(doc -> Product.builder()
                        .productId(new ProductId(UUID.fromString(doc.getId())))
                        .name(doc.getName())
                        .price(new Money(doc.getPrice()))
                        .quantity(doc.getQuantity())
                        .available(doc.isAvailable())
                        .build())
                .collect(Collectors.toList());
    }
}
