package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.persistence.entity.BusinessEntity;
import kh.edu.istad.platform.order.persistence.repository.BusinessReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class BusinessTest {

    @Autowired
    private BusinessReactiveRepository businessReactiveRepository;

    @Test
    void testFindBusiness() {
        Flux<BusinessEntity> biz = businessReactiveRepository.findAll();
        biz.map(businessEntity -> {
            System.out.println("Test: " + businessEntity);
                    return businessEntity;
        })
                .subscribe();
    }

}
