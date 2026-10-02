package br.com.raizesdonordeste.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_BEARER = "bearerAuth";

    @Bean
    public OpenAPI raizesDoNordesteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Raízes do Nordeste")
                        .version("v1")
                        .description(
                                "API REST para pedidos, cardápio, estoque, "
                                        + "pagamento mock e operação das unidades."
                        )
                        .contact(new Contact().name("Projeto Back-End")))
                .components(new Components().addSecuritySchemes(
                        ESQUEMA_BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BEARER));
    }
}
