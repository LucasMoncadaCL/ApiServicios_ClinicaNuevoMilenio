package com.clinicanuevomilenio.solicitudservicio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient usuarioClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8000/api/usuarios").build();
    }

    @Bean
    public WebClient pabellonClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8003/api/pabellones").build();
    }

    @Bean
    public WebClient equipamientoClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8004/api/equipamiento").build();
    }
}