package com.clinicanuevomilenio.solicitudservicio.controllers;

import com.clinicanuevomilenio.solicitudservicio.dto.TipoIncidenciaDTO; // Necesitarás crear este DTO
import com.clinicanuevomilenio.solicitudservicio.repository.TipoIncidenciaRepository; // Y este Repositorio
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/incidencias/tipos")
@RequiredArgsConstructor
public class TipoIncidenciaController {

    private final TipoIncidenciaRepository tipoIncidenciaRepository;

    @GetMapping
    public ResponseEntity<List<TipoIncidenciaDTO>> listarTiposDeIncidencia() {
        List<TipoIncidenciaDTO> tipos = tipoIncidenciaRepository.findAll().stream()
                .map(ti -> new TipoIncidenciaDTO(ti.getId(), ti.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tipos);
    }
}