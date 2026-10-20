package kh.edu.istad.platform.customer.domain.usecase;


import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class    CreateCustomerSegmentUseCase {
    private final CustomerSegmentRepository customerSegmentRepository;
    public Mono<CustomerSegment> execute(CustomerSegment customerSegment){
        customerSegment.initiateCustomerSegment();
        return customerSegmentRepository.save(customerSegment);
    }
}
