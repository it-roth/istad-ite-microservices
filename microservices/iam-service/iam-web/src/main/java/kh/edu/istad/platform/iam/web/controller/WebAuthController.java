package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.domain.usecase.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebAuthController {

    private final RegisterUserUseCase registerUserUseCase;

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            @RequestParam(name = "registered", required = false) String registered,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        if (registered != null) {
            model.addAttribute("registered", true);
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("givenName") String givenName,
            @RequestParam("familyName") String familyName,
            @RequestParam(name = "locale", defaultValue = "en") String locale,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("givenName", givenName);
            model.addAttribute("familyName", familyName);
            model.addAttribute("locale", locale);
            return "register";
        }

        try {
            RegisterUserCommand command = new RegisterUserCommand(
                    username,
                    email,
                    password,
                    givenName,
                    familyName,
                    locale
            );
            registerUserUseCase.execute(command);
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            log.error("Failed to register user: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("givenName", givenName);
            model.addAttribute("familyName", familyName);
            model.addAttribute("locale", locale);
            return "register";
        }
    }
}

