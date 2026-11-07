package kh.edu.istad.iam.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

@Configuration
@Slf4j
public class TokenConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
       return context -> {
           Authentication authentication = context.getPrincipal();
           log.info("Authentication: {}", authentication);
           if(authentication == null){
               return;
           }
           var principal = authentication.getPrincipal();
           log.info("Principle : {}", principal);
           System.out.println(principal);
           if(principal instanceof UserDetails user){


               if(OAuth2ParameterNames.ACCESS_TOKEN.equals(context.getTokenType().getValue())){
                   context.getClaims()
                           .claims(claim -> {
                               claim.put("preferred_username", user.getUsername());
                               claim.put("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
                           });
               }
           }




       };
    }
}
