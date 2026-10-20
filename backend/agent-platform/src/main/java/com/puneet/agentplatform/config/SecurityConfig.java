
package com.puneet.agentplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.support.BearerTokenAuthenticationExtractor;
import org.springframework.graphql.server.webmvc.AuthenticationWebSocketInterceptor;

import org.springframework.graphql.server.support.AuthenticationExtractor;
import org.springframework.security.authentication.BadCredentialsException;
import reactor.core.publisher.Mono;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        RequestMatcher websocketHandshake = request -> HttpMethod.GET.matches(request.getMethod())
                && "/graphql".equals(request.getRequestURI())
                && "websocket".equalsIgnoreCase(
                        request.getHeader("Upgrade"));

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/graphiql", "/graphiql/**")
                        .permitAll()
                        .requestMatchers(websocketHandshake)
                        .permitAll()
                        .requestMatchers("/graphql")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

            if (realmAccess == null) {
                return List.of();
            }

            Object rolesObject = realmAccess.get("roles");

            if (!(rolesObject instanceof Collection<?> roles)) {
                return List.of();
            }

            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> new SimpleGrantedAuthority(
                            "ROLE_" + role))
                    .map(GrantedAuthority.class::cast)
                    .toList();
        });

        return converter;
    }

    @Bean
    AuthenticationManager websocketAuthenticationManager(
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter) {

        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);

        provider.setJwtAuthenticationConverter(jwtAuthenticationConverter);

        return new ProviderManager(provider);
    }

    @Bean
    WebSocketGraphQlInterceptor authenticationWebSocketInterceptor(
            AuthenticationManager websocketAuthenticationManager) {

        AuthenticationExtractor bearerTokenExtractor = new BearerTokenAuthenticationExtractor();

        AuthenticationExtractor requiredBearerTokenExtractor = payload -> bearerTokenExtractor
                .getAuthentication(payload)
                .switchIfEmpty(Mono.error(
                        new BadCredentialsException(
                                "Missing WebSocket bearer token")));

        return new AuthenticationWebSocketInterceptor(
                requiredBearerTokenExtractor,
                websocketAuthenticationManager);
    }

}
