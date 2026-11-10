package kh.edu.istad.platform.business;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {
    @GetMapping({"/api/demo"})
    public Map<String, String> demo() {
        return Map.of("message", "Hello from Business Service");
    }
}
