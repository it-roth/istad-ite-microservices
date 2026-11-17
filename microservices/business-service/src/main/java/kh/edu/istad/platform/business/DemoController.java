package kh.edu.istad.platform.business;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    @GetMapping
    public Map<String, String> demo(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Hello from Business Service",
                "asccessToken", jwt.getTokenValue()
        );
    }
}
