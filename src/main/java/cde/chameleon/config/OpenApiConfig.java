package cde.chameleon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final String authorizationUri;
    private final String tokenUri;

    @Autowired
    public OpenApiConfig(
            @Value("${spring.security.oauth2.client.provider.chameleon.authorization-uri}") String authorizationUri,
            @Value("${spring.security.oauth2.client.provider.chameleon.token-uri}") String tokenUri) {
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
    }

    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chameleon API")
                        .description("This is the API of the project Chameleon.")
                        .version("1.0"))
                .components(
                        new Components()
                                .addSecuritySchemes("OAuth2", new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("Oauth2 Flow")
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(authorizationUri)
                                                        .refreshUrl(tokenUri)
                                                        .tokenUrl(tokenUri)
                                                        .scopes(new Scopes())))))
                .security(List.of(new SecurityRequirement().addList("OAuth2")));
    }
}
