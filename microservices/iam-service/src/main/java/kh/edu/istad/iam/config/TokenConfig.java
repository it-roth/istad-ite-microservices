package kh.edu.istad.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class TokenConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
       return context -> {
           System.out.println("HIttttt");
           SecurityContext cn = SecurityContextHolder.getContext();
           Authentication authentication = cn.getAuthentication();
           System.out.println(cn);
           if(authentication == null){
               return;
           }
           System.out.println(authentication);
           var principal = authentication.getPrincipal();
           System.out.println(principal);
           if(principal instanceof UserDetails user){
               if(OAuth2ParameterNames.ACCESS_TOKEN.equals(context.getTokenType().getValue())){
                   context.getClaims()
                           .claims(claim -> {
                               claim.put("preferred_username", user.getUsername());
                               claim.put("authorities", user.getAuthorities());
                           });
               }
           }




       };
    }
}
