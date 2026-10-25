package kh.edu.istad.platform.order.domain.port.in;

import kh.edu.istad.platform.order.domain.dto.CommunicationTestResult;

public interface TestOrderCommunicationUseCase {
    CommunicationTestResult test(boolean useEureka);
}
