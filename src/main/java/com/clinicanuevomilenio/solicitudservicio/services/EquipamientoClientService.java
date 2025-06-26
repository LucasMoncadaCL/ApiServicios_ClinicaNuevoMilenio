package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.EquipamientoDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipamientoClientService {

    private final WebClient webClient;

    // Constructor explícito para inyectar el WebClient correcto
    public EquipamientoClientService(@Qualifier("equipamientoClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Llama a la equipamiento-api para obtener los detalles de un equipo por su ID.
     * @param id El ID del equipo a buscar.
     * @return Un DTO con la información del equipo, o null si no se encuentra.
     */
    public EquipamientoDTO obtenerEquipamientoPorId(Integer id) {
        try {
            return webClient.get()
                    // Apunta al endpoint correcto en EquipamientoController
                    .uri("/tipos/{id}", id)
                    .retrieve()
                    .bodyToMono(EquipamientoDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de equipamiento: " + e.getMessage());
        }
    }

    /**
     * Llama a la equipamiento-api para obtener una lista de equipos a partir de sus IDs.
     * @param ids Una lista de IDs de equipamientos.
     * @return Una lista de DTOs con la información de los equipos encontrados.
     */
    public List<EquipamientoDTO> obtenerEquipamientosPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String idsComoString = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            // Apunta al endpoint correcto en EquipamientoController
                            .path("/tipos/por-ids")
                            .queryParam("ids", idsComoString)
                            .build())
                    .retrieve()
                    .bodyToFlux(EquipamientoDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            System.err.println("Error al obtener equipamientos por IDs: " + e.getMessage());
            return List.of();
        }
    }
}