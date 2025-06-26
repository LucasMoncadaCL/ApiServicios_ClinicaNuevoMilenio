package com.clinicanuevomilenio.solicitudservicio.controllers;

import com.clinicanuevomilenio.solicitudservicio.dto.PrioridadSolicitudDTO;
import com.clinicanuevomilenio.solicitudservicio.repository.PrioridadSolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes-servicio/prioridades")
@RequiredArgsConstructor
public class PrioridadSolicitudController {

    private final PrioridadSolicitudRepository prioridadRepository;

    @GetMapping
    public ResponseEntity<List<PrioridadSolicitudDTO>> listarPrioridades() {
        List<PrioridadSolicitudDTO> prioridades = prioridadRepository.findAll().stream()
                .map(p -> {
                    PrioridadSolicitudDTO dto = new PrioridadSolicitudDTO();
                    dto.setId(p.getId());
                    dto.setNombre(p.getNombre());
                    dto.setNivel(p.getNivel());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(prioridades);
    }
}