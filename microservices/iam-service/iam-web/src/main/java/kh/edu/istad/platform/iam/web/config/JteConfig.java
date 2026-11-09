package kh.edu.istad.platform.iam.web.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.WriterOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
public class JteConfig {

    @Bean
    public TemplateEngine templateEngine() {
        Path devPath = Paths.get("microservices/iam-service/iam-web/src/main/jte");
        if (Files.exists(devPath)) {
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(devPath);
            return TemplateEngine.create(codeResolver, Paths.get("build/jte-classes"), ContentType.Html, getClass().getClassLoader());
        }

        Path localPath = Paths.get("src/main/jte");
        if (Files.exists(localPath)) {
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(localPath);
            return TemplateEngine.create(codeResolver, Paths.get("build/jte-classes"), ContentType.Html, getClass().getClassLoader());
        }

        ResourceCodeResolver codeResolver = new ResourceCodeResolver("jte");
        return TemplateEngine.create(codeResolver, Paths.get("build/jte-classes"), ContentType.Html, getClass().getClassLoader());
    }

    @Bean
    public ViewResolver jteViewResolver(TemplateEngine templateEngine) {
        return new JteViewResolver(templateEngine);
    }

    private static final class JteViewResolver implements ViewResolver, Ordered {

        private final TemplateEngine templateEngine;

        private JteViewResolver(TemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) {
            if (viewName.startsWith("redirect:") || viewName.startsWith("forward:")) {
                return null;
            }

            String template = viewName.endsWith(".jte") ? viewName : viewName + ".jte";
            return (Map<String, ?> model, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) -> {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    Map<String, Object> parameters = new LinkedHashMap<>();
                    parameters.putAll(model);
                    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
                    if (csrfToken != null) {
                        parameters.putIfAbsent("csrfToken", csrfToken);
                    }
                    templateEngine.render(template, parameters, new WriterOutput(writer));
                }
            };
        }
    }
}
