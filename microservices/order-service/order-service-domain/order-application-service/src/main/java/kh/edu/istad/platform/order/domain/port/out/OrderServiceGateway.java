package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.dto.CommunicationTestResult;

public interface OrderServiceGateway {
    CommunicationTestResult testCommunication(boolean useEureka);
}
