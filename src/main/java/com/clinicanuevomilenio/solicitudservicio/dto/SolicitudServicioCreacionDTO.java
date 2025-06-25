package com.clinicanuevomilenio.solicitudservicio.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudServicioCreacionDTO {
    private String tipo; // "Mantenimiento", "Aseo", etc.
    private String descripcion;
    private LocalDateTime fechaHrRequerida;
    private Integer prioridadId;
    private Integer pabellonId;
    private Integer equipamientoId; // Opcional
    private String comentarios;
}