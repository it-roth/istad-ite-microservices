package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.entity.Product;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.persistence.mapper.BusinessPersistenceMapper;
import kh.edu.istad.platform.business.persistence.repository.BusinessReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BusinessRepositoryAdapter implements BusinessRepository {

    private final BusinessReactiveMongoRepository businessReactiveMongoRepository;
    private final BusinessPersistenceMapper businessPersistenceMapper;

    private final Map<String, Business> memoryCache = new ConcurrentHashMap<>();

    {
        // Seed default demo business & products
        UUID businessUuid = UUID.fromString("c74384b6-d5a8-48be-81f1-3253b8112211");
        Product p1 = Product.builder()
                .productId(new ProductId(UUID.fromString("9a12c345-6789-4abc-def0-1234567890ab")))
                .name("MacBook Pro M4 16-inch")
                .price(new Money(new BigDecimal("2499.00")))
                .quantity(50)
                .available(true)
                .build();
        Product p2 = Product.builder()
                .productId(new ProductId(UUID.fromString("8b23d456-789a-4bcd-ef01-2345678901bc")))
                .name("Logitech MX Master 3S")
                .price(new Money(new BigDecimal("99.00")))
                .quantity(100)
                .available(true)
                .build();

        Business defaultBusiness = Business.builder()
                .businessId(new BusinessId(businessUuid))
                .businessName("TechZone Electronics")
                .businessContact(new BusinessContact("techzone@istad.edu.kh", "+85523888999"))
                .products(List.of(p1, p2))
                .active(true)
                .build();

        memoryCache.put(businessUuid.toString(), defaultBusiness);
    }

    @Override
    public Mono<Business> save(Business business) {
        String key = business.getId() != null ? business.getId().value().toString() : UUID.randomUUID().toString();
        memoryCache.put(key, business);

        try {
            var document = businessPersistenceMapper.toDocument(business);
            return businessReactiveMongoRepository.save(document)
                    .doOnSuccess(saved -> log.info("Business saved in mongo with id: {}", saved.getId()))
                    .map(saved -> business)
                    .onErrorReturn(business);
        } catch (Exception e) {
            log.warn("Mongo unavailable, stored business in in-memory cache: {}", key);
            return Mono.just(business);
        }
    }

    @Override
    public Mono<Business> findById(String id) {
        return businessReactiveMongoRepository.findById(id)
                .map(businessPersistenceMapper::toAggregate)
                .onErrorResume(e -> {
                    log.warn("Mongo error, reading business from memory cache: {}", id);
                    Business b = memoryCache.get(id);
                    return b != null ? Mono.just(b) : Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Business b = memoryCache.get(id);
                    return b != null ? Mono.just(b) : Mono.empty();
                }));
    }

    @Override
    public Flux<Business> findAll() {
        return businessReactiveMongoRepository.findAll()
                .map(businessPersistenceMapper::toAggregate)
                .onErrorResume(e -> {
                    log.warn("Mongo error, reading all businesses from memory cache");
                    return Flux.fromIterable(memoryCache.values());
                });
    }
}
