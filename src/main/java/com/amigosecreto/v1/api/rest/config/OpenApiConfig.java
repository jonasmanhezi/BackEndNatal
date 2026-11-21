package com.amigosecreto.v1.api.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String PARTICIPANT_TOKEN = "participantToken";
    public static final String ADMIN_KEY = "adminKey";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API do Amigo Secreto de Natal")
                        .description("""
                                Backend do site do Amigo Secreto. Um grupo por instalação, ninguém precisa de conta.

                                Para testar:
                                1. Rotas de organizador: clique em Authorize e cole a ADMIN_KEY em adminKey
                                2. Cadastre alguém em POST /api/participants e copie o token da resposta
                                3. Rotas /api/me: clique em Authorize e cole o token em participantToken
                                """)
                        .version("v1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(PARTICIPANT_TOKEN, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description("Token pessoal devolvido no cadastro"))
                        .addSecuritySchemes(ADMIN_KEY, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Admin-Key")
                                .description("Chave do organizador (variável ADMIN_KEY)")));
    }
}
