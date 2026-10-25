package kh.edu.istad.platform.order.restapi.controller;

import kh.edu.istad.platform.order.domain.dto.CommunicationTestResult;
import kh.edu.istad.platform.order.domain.port.in.TestOrderCommunicationUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders/communication-test")
public class OrderCommunicationController {
    private final TestOrderCommunicationUseCase testOrderCommunicationUseCase;

    public OrderCommunicationController(TestOrderCommunicationUseCase testOrderCommunicationUseCase) {
        this.testOrderCommunicationUseCase = testOrderCommunicationUseCase;
    }

    @GetMapping("/hard-coded")
    public CommunicationTestResult testHardCodedCommunication() {
        return testOrderCommunicationUseCase.test(false);
    }

    @GetMapping("/eureka")
    public CommunicationTestResult testEurekaCommunication() {
        return testOrderCommunicationUseCase.test(true);
    }
}
