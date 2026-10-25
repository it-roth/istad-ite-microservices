package kh.edu.istad.platform.business.persistence.adapter;

import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.persistence.mapper.BusinessPersistenceMapper;
import kh.edu.istad.platform.business.persistence.repository.BusinessReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BusinessRepositoryAdapter implements BusinessRepository {

    private final BusinessReactiveMongoRepository businessReactiveMongoRepository;
    private final BusinessPersistenceMapper businessPersistenceMapper;

    @Override
    public Mono<Business> save(Business business) {
        var document = businessPersistenceMapper.toDocument(business);
        return businessReactiveMongoRepository.save(document)
                .doOnSuccess(saved -> log.info("Business saved with id: {}", saved.getId()))
                .map(saved -> business);
    }
}
