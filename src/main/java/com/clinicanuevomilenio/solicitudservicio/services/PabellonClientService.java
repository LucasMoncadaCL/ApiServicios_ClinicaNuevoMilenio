package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.PabellonDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PabellonClientService {

    private final WebClient webClient;

    // Constructor explícito para inyectar el WebClient correcto
    public PabellonClientService(@Qualifier("pabellonClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Llama a la pabellones-api para obtener los detalles de un pabellón por su ID.
     * @param id El ID del pabellón a buscar.
     * @return Un DTO con la información del pabellón, o null si no se encuentra.
     */
    public PabellonDTO obtenerPabellonPorId(Integer id) {
        try {
            return webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(PabellonDTO.class)
                    .block(); // block() hace la llamada síncrona
        } catch (WebClientResponseException.NotFound e) {
            return null; // Si la otra API devuelve 404, retornamos null
        } catch (Exception e) {
            // Lanza una excepción genérica si falla la comunicación
            throw new RuntimeException("Error al comunicarse con el servicio de pabellones: " + e.getMessage());
        }
    }

    /**
     * Llama a la pabellones-api para obtener una lista de pabellones a partir de sus IDs.
     * @param ids Una lista de IDs de pabellones.
     * @return Una lista de DTOs con la información de los pabellones encontrados.
     */
    public List<PabellonDTO> obtenerPabellonesPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of(); // Devuelve lista vacía si no hay IDs que buscar
        }

        // Convierte la lista de Integers a un solo String separado por comas (ej: "1,2,3")
        // Este formato es más robusto para las peticiones entre servicios.
        String idsComoString = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/por-ids")
                            .queryParam("ids", idsComoString) // Se envía como un solo parámetro
                            .build())
                    .retrieve()
                    .bodyToFlux(PabellonDTO.class) // Se usa bodyToFlux para recibir una lista
                    .collectList()
                    .block();
        } catch (Exception e) {
            System.err.println("Error al obtener pabellones por IDs: " + e.getMessage());
            return List.of(); // Devuelve lista vacía en caso de error
        }
    }
}