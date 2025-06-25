package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.EquipamientoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipamientoClientService {

    // Inyecta el bean específico para el cliente de equipamiento
    private final WebClient equipamientoClient;

    /**
     * Obtiene los detalles de un único equipo desde la equipamiento-api.
     */
    public EquipamientoDTO obtenerEquipamientoPorId(Integer equipamientoId) {
        try {
            return equipamientoClient.get()
                    .uri("/tipos/{id}", equipamientoId) // Asume que la ruta es /tipos/{id} en la otra API
                    .retrieve()
                    .bodyToMono(EquipamientoDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null; // Devuelve null si no se encuentra
        }
    }

    /**
     * Obtiene una lista de equipos a partir de una lista de IDs en una sola llamada.
     */
    public List<EquipamientoDTO> obtenerEquipamientosPorIds(List<Integer> equipamientoIds) {
        if (equipamientoIds == null || equipamientoIds.isEmpty()) {
            return List.of();
        }
        return equipamientoClient.get()
                .uri(uriBuilder -> uriBuilder.path("/tipos/por-ids").queryParam("ids", equipamientoIds).build()) // Asume que la ruta masiva es /tipos/por-ids
                .retrieve()
                .bodyToFlux(EquipamientoDTO.class)
                .collectList()
                .block();
    }
}