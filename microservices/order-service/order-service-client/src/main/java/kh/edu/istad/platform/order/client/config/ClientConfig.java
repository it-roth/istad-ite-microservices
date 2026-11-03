package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

        @Value("${services.customer.url:http://localhost:9091}")
        private String customerServiceUrl;

        @Bean
        public ReactiveClientRegistrationRepository clientRegistrationRepository() {
                ClientRegistration clientRegistration = ClientRegistration
                                .withRegistrationId("order-service")
                                .clientId("order-service")
                                .clientSecret("order-secret")
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                .tokenUri("http://localhost:9000/oauth2/token")
                                .scope("read", "write")
                                .build();

                return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
        }

        @Bean
        public ReactiveOAuth2AuthorizedClientService authorizedClientService(
                        ReactiveClientRegistrationRepository clientRegistrationRepository) {
                return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        public ReactiveOAuth2AuthorizedClientManager reactiveAuthorizedClientManager(
                        ReactiveClientRegistrationRepository clientRegistrationRepository,
                        ReactiveOAuth2AuthorizedClientService authorizedClientService) {

                ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder
                                .builder()
                                .clientCredentials()
                                .build();

                AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                                clientRegistrationRepository, authorizedClientService);
                authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

                return authorizedClientManager;
        }

        @Bean
        public WebClient customerWebClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
                ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                                authorizedClientManager);
                oauthFilter.setDefaultClientRegistrationId("order-service");

                return WebClient.builder()
                                .baseUrl(customerServiceUrl)
                                .filter(oauthFilter)
                                .build();
        }

        @Bean
        public CustomerHttpClient customerHttpClient(WebClient customerWebClient) {
                HttpServiceProxyFactory factory = HttpServiceProxyFactory
                                .builderFor(WebClientAdapter.create(customerWebClient))
                                .build();
                return factory.createClient(CustomerHttpClient.class);
        }
}