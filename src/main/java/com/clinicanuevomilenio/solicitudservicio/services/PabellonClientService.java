package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.PabellonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PabellonClientService {

    // Inyecta el bean específico para el cliente de pabellones
    private final WebClient pabellonClient;

    /**
     * Obtiene los detalles de un único pabellón desde la pabellones-api.
     */
    public PabellonDTO obtenerPabellonPorId(Integer pabellonId) {
        try {
            return pabellonClient.get()
                    .uri("/{id}", pabellonId)
                    .retrieve()
                    .bodyToMono(PabellonDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null; // Devuelve null si no se encuentra
        }
    }

    /**
     * Obtiene una lista de pabellones a partir de una lista de IDs en una sola llamada.
     */
    public List<PabellonDTO> obtenerPabellonesPorIds(List<Integer> pabellonIds) {
        if (pabellonIds == null || pabellonIds.isEmpty()) {
            return List.of();
        }
        return pabellonClient.get()
                .uri(uriBuilder -> uriBuilder.path("/por-ids").queryParam("ids", pabellonIds).build())
                .retrieve()
                .bodyToFlux(PabellonDTO.class)
                .collectList()
                .block();
    }
}