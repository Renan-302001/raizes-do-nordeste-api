package br.com.raizesdonordeste.api.config;

import br.com.raizesdonordeste.api.infrastructure.security.RestAccessDeniedHandler;
import br.com.raizesdonordeste.api.infrastructure.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    )
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/cadastro",
                                "/api/v1/auth/login",
                                "/api/v1/pedidos"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/produtos",
                                "/api/v1/produtos/**",
                                "/api/v1/unidades",
                                "/api/v1/unidades/*",
                                "/api/v1/unidades/*/cardapios/ativo"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/produtos"
                        ).hasRole("ADMIN_MATRIZ")
                        .requestMatchers(
                                "/api/v1/unidades/*/estoques/**"
                        ).hasAnyRole("GERENTE", "ADMIN_MATRIZ")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/unidades/*/cardapios",
                                "/api/v1/cardapios/*/itens"
                        ).hasAnyRole("GERENTE", "ADMIN_MATRIZ")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/pedidos"
                        ).hasAnyRole("GERENTE", "ADMIN_MATRIZ")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/pedidos/*/status"
                        ).hasAnyRole("COZINHA", "ATENDENTE", "ADMIN_MATRIZ")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/pedidos/*/cancelar"
                        ).hasAnyRole(
                                "CLIENTE",
                                "ATENDENTE",
                                "GERENTE",
                                "ADMIN_MATRIZ"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/pedidos/*/pagamentos"
                        ).hasAnyRole("CLIENTE", "ATENDENTE")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return authenticationConverter;
    }
}
