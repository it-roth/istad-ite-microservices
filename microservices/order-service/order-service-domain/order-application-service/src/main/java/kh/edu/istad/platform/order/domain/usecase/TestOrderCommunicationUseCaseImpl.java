package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.CommunicationTestResult;
import kh.edu.istad.platform.order.domain.port.in.TestOrderCommunicationUseCase;
import kh.edu.istad.platform.order.domain.port.out.OrderServiceGateway;

public class TestOrderCommunicationUseCaseImpl implements TestOrderCommunicationUseCase {
    private final OrderServiceGateway orderServiceGateway;

    public TestOrderCommunicationUseCaseImpl(OrderServiceGateway orderServiceGateway) {
        this.orderServiceGateway = orderServiceGateway;
    }

    @Override
    public CommunicationTestResult test(boolean useEureka) {
        return orderServiceGateway.testCommunication(useEureka);
    }
}
