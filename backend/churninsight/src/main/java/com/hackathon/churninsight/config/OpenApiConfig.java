package com.hackathon.churninsight.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI churnInsightOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChurnInsight API")
                        .description("""
                                API REST para predicción de churn de clientes
                                en plataformas de streaming.
                                
                                Consume un servicio externo de Machine Learning
                                para generar predicciones.
                                """)
                        .version("1.0.0"));
    }
}

