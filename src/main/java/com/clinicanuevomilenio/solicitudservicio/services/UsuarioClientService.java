package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioClientService {

    // El campo ya no necesita @Autowired aquí
    private final WebClient webClient;

    // Se crea un constructor explícito.
    // @Qualifier aquí le dice a Spring cuál de los 3 WebClient beans debe usar.
    public UsuarioClientService(@Qualifier("usuarioClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public UsuarioDTO obtenerUsuarioPorId(Integer idUsuario) {
        try {
            return webClient.get()
                    .uri("/{id}", idUsuario)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de usuarios: " + e.getMessage());
        }
    }

    public List<UsuarioDTO> obtenerUsuariosPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String idsComoString = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/por-ids")
                            .queryParam("ids", idsComoString)
                            .build())
                    .retrieve()
                    .bodyToFlux(UsuarioDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios por IDs: " + e.getMessage());
            return List.of();
        }
    }

    public List<UsuarioDTO> obtenerUsuariosPorRol(String rol) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/por-rol")
                            .queryParam("rol", rol)
                            .build())
                    .retrieve()
                    .bodyToFlux(UsuarioDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios por rol: " + rol + " - " + e.getMessage());
            return List.of();
        }
    }
}