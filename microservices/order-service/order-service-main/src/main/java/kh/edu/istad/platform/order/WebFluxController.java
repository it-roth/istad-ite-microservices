package kh.edu.istad.platform.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class WebFluxController {

    private static final Logger log = LoggerFactory.getLogger(WebFluxController.class);

    @GetMapping("/flux")
    public Flux<String> test() {
        return Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")
                .log()
                .delayElements(Duration.ofSeconds(1));
    }
}