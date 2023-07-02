package cde.chameleon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class WebSecurityConfig {

    private final String userNameAttribute;

    public WebSecurityConfig(
            @Value("${spring.security.oauth2.client.provider.chameleon.user-name-attribute}") String userNameAttribute) {
        this.userNameAttribute = userNameAttribute;
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtToAuthenticationTokenConverter() {
        return jwt -> {
            // username
            String username = (String) jwt.getClaims().get(userNameAttribute);
            // roles
            @SuppressWarnings("unchecked")
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("realm_access", Map.of());
            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.getOrDefault("roles", List.of());
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> "ROLE_" + role) // prefix to map to a role in Spring Security
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return new JwtAuthenticationToken(jwt, authorities, username);
        };
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(resourceServer ->
            resourceServer.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtToAuthenticationTokenConverter())));
        http.anonymous(Customizer.withDefaults());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // disable sessions (i.e. service is stateless)
        http.sessionManagement(sessionManager ->
            sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // disable CSRF because authentication and authorization is not based on cookies
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/*").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health/readiness").permitAll()
                .requestMatchers("/actuator/health/liveness").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
