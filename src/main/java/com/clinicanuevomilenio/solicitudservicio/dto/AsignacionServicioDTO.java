package com.clinicanuevomilenio.solicitudservicio.dto;

import com.clinicanuevomilenio.solicitudservicio.dto.UsuarioDTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AsignacionServicioDTO {
    private Integer id;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFinalizacion;
    private String estado;
    private UsuarioDTO usuarioAsignado; // Objeto completo del usuario asignado
}