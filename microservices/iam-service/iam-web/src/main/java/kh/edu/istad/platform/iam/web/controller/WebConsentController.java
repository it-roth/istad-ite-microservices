package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.domain.dto.OAuthConsentResult;
import kh.edu.istad.platform.iam.domain.usecase.GetOAuthConsentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebConsentController {

    private final GetOAuthConsentUseCase getOAuthConsentUseCase;

    @GetMapping("/oauth2/consent")
    public String consent(
            Principal principal,
            Model model,
            @RequestParam("client_id") String clientId,
            @RequestParam("scope") String scope,
            @RequestParam("state") String state,
            @RequestParam(name = "user_code", required = false) String userCode
    ) {
        Set<String> requestedScopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " "))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        OAuthConsentResult consent = getOAuthConsentUseCase.execute(
                clientId,
                principal.getName(),
                requestedScopes
        );

        model.addAttribute("clientId", consent.clientId());
        model.addAttribute("clientName", consent.clientName());
        model.addAttribute("state", state);
        model.addAttribute("scopes", consent.requestedScopes());
        model.addAttribute("previouslyApprovedScopes", consent.previouslyApprovedScopes());
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("userCode", userCode);

        return "consent";
    }
}
