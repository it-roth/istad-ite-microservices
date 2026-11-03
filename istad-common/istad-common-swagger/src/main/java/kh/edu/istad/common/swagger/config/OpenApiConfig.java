package kh.edu.istad.common.swagger.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@OpenAPIDefinition(
        info = @Info(
                title = "ISTAD ITE PLATFORM",
                description = "Saroth is a spider-man"
        )
)
public class OpenApiConfig {

}
