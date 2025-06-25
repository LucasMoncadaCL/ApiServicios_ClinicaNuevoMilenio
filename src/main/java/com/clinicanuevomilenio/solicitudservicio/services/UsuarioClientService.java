package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioClientService {
    private final WebClient usuarioClient;

    /**
     * Obtiene los detalles de un único usuario desde la usuarios-api.
     * @param idUsuario El ID del usuario a buscar.
     * @return El DTO del usuario o null si no se encuentra.
     */
    public UsuarioDTO obtenerUsuarioPorId(Integer idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        try {
            return usuarioClient.get()
                    .uri("/{id}", idUsuario) // Llama a GET /api/usuarios/{id}
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            // Si la otra API devuelve un 404 Not Found, nosotros simplemente devolvemos null.
            return null;
        }
    }

    /**
     * Obtiene una lista de usuarios a partir de una lista de IDs en una sola llamada.
     */
    public List<UsuarioDTO> obtenerUsuariosPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return usuarioClient.get()
                .uri(uriBuilder -> uriBuilder.path("/por-ids").queryParam("ids", ids).build())
                .retrieve()
                .bodyToFlux(UsuarioDTO.class)
                .collectList()
                .block();
    }
}