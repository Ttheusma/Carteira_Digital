package com.projeto.carteiradigital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API da Carteira Digital")
                        .version("v1.0.0")
                        .description("Motor financeiro ACID com suporte a transações P2P, câmbio de múltiplas moedas e auditoria imutável."));
    }
}